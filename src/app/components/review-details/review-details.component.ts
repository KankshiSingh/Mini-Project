import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { catchError, forkJoin, of } from 'rxjs';
import { ReviewService, Review } from '../../services/review.service';
import { BookService } from '../../services/book.service';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-review-details',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './review-details.component.html',
  styleUrls: ['./review-details.component.css'],
  // Force default change detection — NOT OnPush — so signals propagate
})
export class ReviewDetailsComponent implements OnInit {
  private route       = inject(ActivatedRoute);
  private reviewSvc   = inject(ReviewService);
  private bookSvc     = inject(BookService);
  private authService = inject(AuthService);
  private toast       = inject(ToastService);

  // ── Reactive state via signals ──────────────────────────────────────
  loading = signal(true);
  book    = signal<any>(null);
  reviews = signal<Review[]>([]);

  bookId: string | null = null;

  // New review form (plain values — not signals, no async concern)
  newReviewText = '';
  newRating     = 0;
  hoverRating   = 0;

  // ── Derived ────────────────────────────────────────────────────────
  avgRating = computed(() => {
    const rs = this.reviews();
    if (!rs.length) return 0;
    return +(rs.reduce((s, r) => s + r.rating, 0) / rs.length).toFixed(1);
  });

  ratingDist = computed(() => {
    const rs = this.reviews();
    return [5, 4, 3, 2, 1].map(n => {
      const count = rs.filter(r => r.rating === n).length;
      const pct   = rs.length ? Math.round((count / rs.length) * 100) : 0;
      return { star: n, count, pct };
    });
  });

  get isLoggedIn(): boolean { return !!this.authService.currentUser(); }
  get currentUser(): any    { return this.authService.currentUser(); }

  // ── Lifecycle ──────────────────────────────────────────────────────
  ngOnInit(): void {
    this.bookId = this.route.snapshot.paramMap.get('bookId');
    if (this.bookId) {
      this.load();
    } else {
      this.loading.set(false);
    }
  }

  load() {
    if (!this.bookId) { this.loading.set(false); return; }
    this.loading.set(true);

    forkJoin({
      book:    this.bookSvc.getBookById(this.bookId).pipe(catchError(() => of(null))),
      reviews: this.reviewSvc.getReviewsForBook(this.bookId).pipe(catchError(() => of([] as Review[])))
    }).subscribe({
      next: ({ book, reviews }) => {
        this.book.set(book);
        this.reviews.set(reviews ?? []);
        this.loading.set(false);          // signals trigger CD automatically
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  // ── Star interaction ───────────────────────────────────────────────
  setHover(v: number) { this.hoverRating = v; }
  setRating(v: number) { this.newRating  = v; }

  // ── Submit review ──────────────────────────────────────────────────
  submitReview() {
    if (!this.newRating) { this.toast.error('Please select a star rating.'); return; }
    const user = this.currentUser;
    if (!user)           { this.toast.error('Please log in first.'); return; }

    const review: Review = {
      bookId:     this.bookId!,
      userId:     user.id || user._id,
      username:   user.name || user.username || 'Anonymous',
      rating:     this.newRating,
      reviewText: this.newReviewText,
    };

    this.reviewSvc.addReview(review).subscribe({
      next: () => {
        this.toast.success('Review posted!');
        this.newReviewText = '';
        this.newRating     = 0;
        this.load();
      },
      error: () => this.toast.error('Failed to post review.')
    });
  }

  // ── Delete review ──────────────────────────────────────────────────
  deleteReview(id: string) {
    if (!confirm('Delete this review?')) return;
    this.reviewSvc.deleteReview(id).subscribe({
      next: () => { this.toast.success('Review deleted.'); this.load(); },
      error: () => this.toast.error('Failed to delete review.')
    });
  }
}
