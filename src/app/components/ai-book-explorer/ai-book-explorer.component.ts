import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-ai-book-explorer',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './ai-book-explorer.component.html',
  styleUrls: ['./ai-book-explorer.component.css']
})
export class AiBookExplorerComponent {

  searchText = '';
  books: any[] = [];

  constructor(private http: HttpClient){}

  searchBooks(){

    const url =
    `https://www.googleapis.com/books/v1/volumes?q=${this.searchText}`;

    this.http.get(url).subscribe((data:any)=>{

      this.books = data.items;

    });

  }

}
