package com.bookrevive.config;

import com.bookrevive.model.Book;
import com.bookrevive.model.Review;
import com.bookrevive.model.User;
import com.bookrevive.repository.BookRepository;
import com.bookrevive.repository.ReviewRepository;
import com.bookrevive.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        bookRepository.deleteAll();
        reviewRepository.deleteAll();

        // Seed a default user if none exists
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("password123"));
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            userRepository.save(admin);
            logger.info("Default admin user created: admin@example.com / password123");
        }

        logger.info("Database cleared. Seeding 250 popular books and initial reviews...");

        List<Book> books = new ArrayList<>();
        Random random = new Random();

        // Fiction (25)
        seedGenre(books, "Fiction", new String[][]{
            {"To Kill a Mockingbird", "Harper Lee", "9780061120084"},
            {"1984", "George Orwell", "9780451524935"},
            {"The Great Gatsby", "F. Scott Fitzgerald", "9780743273565"},
            {"The Hobbit", "J.R.R. Tolkien", "9780261102217"},
            {"Pride and Prejudice", "Jane Austen", "9780141439518"},
            {"The Catcher in the Rye", "J.D. Salinger", "9780316769488"},
            {"The Alchemist", "Paulo Coelho", "9780062315007"},
            {"Beloved", "Toni Morrison", "9781400033416"},
            {"One Hundred Years of Solitude", "Gabriel García Márquez", "9780060883287"},
            {"Little Women", "Louisa May Alcott", "9780147514011"},
            {"Brave New World", "Aldous Huxley", "9780060850524"},
            {"The Kite Runner", "Khaled Hosseini", "9781594631931"},
            {"The Book Thief", "Markus Zusak", "9780375831003"},
            {"Animal Farm", "George Orwell", "9780451526342"},
            {"Fahrenheit 451", "Ray Bradbury", "9781451673319"},
            {"Jane Eyre", "Charlotte Brontë", "9780141441146"},
            {"The Grapes of Wrath", "John Steinbeck", "9780143039433"},
            {"Wuthering Heights", "Emily Brontë", "9780141439556"},
            {"The Picture of Dorian Gray", "Oscar Wilde", "9780141439570"},
            {"Lord of the Flies", "William Golding", "9780399501487"},
            {"Lessons in Chemistry", "Bonnie Garmus", "9780385547345"},
            {"The Handmaid's Tale", "Margaret Atwood", "9780385490818"},
            {"Life of Pi", "Yann Martel", "9780156027328"},
            {"The Shadow of the Wind", "Carlos Ruiz Zafón", "9780143034902"},
            {"Normal People", "Sally Rooney", "9781984822178"}
        }, random);

        // Self-Help (25)
        seedGenre(books, "Self-Help", new String[][]{
            {"Atomic Habits", "James Clear", "9780735211292"},
            {"The 7 Habits of Highly Effective People", "Stephen Covey", "9781982137274"},
            {"How to Win Friends & Influence People", "Dale Carnegie", "9780671027032"},
            {"The Subtle Art of Not Giving a F*ck", "Mark Manson", "9780062457714"},
            {"Man's Search for Meaning", "Viktor Frankl", "9780807014271"},
            {"The Power of Now", "Eckhart Tolle", "9781577314806"},
            {"Daring Greatly", "Brené Brown", "9781592408412"},
            {"You Are a Badass", "Jen Sincero", "9780762447695"},
            {"Mindset", "Carol Dweck", "9780345472328"},
            {"The Four Agreements", "Don Miguel Ruiz", "9781878424310"},
            {"Think and Grow Rich", "Napoleon Hill", "9781585424337"},
            {"Can't Hurt Me", "David Goggins", "9781544512280"},
            {"The Mountain Is You", "Brianna Wiest", "9781949759228"},
            {"Boundary Boss", "Terri Cole", "9781683647683"},
            {"Good Vibes, Good Life", "Vex King", "9781788171823"},
            {"The Gifts of Imperfection", "Brené Brown", "9781592408214"},
            {"Limitless", "Jim Kwik", "9781401958237"},
            {"Indistractable", "Nir Eyal", "9781948836531"},
            {"Digital Minimalism", "Cal Newport", "9780525536512"},
            {"The Comfort Book", "Matt Haig", "9780593297483"},
            {"Big Magic", "Elizabeth Gilbert", "9781594634727"},
            {"Quiet", "Susan Cain", "9780307352156"},
            {"12 Rules for Life", "Jordan Peterson", "9780345816022"},
            {"The Boy, the Mole, the Fox and the Horse", "Charlie Mackesy", "9780062971395"},
            {"Start with Why", "Simon Sinek", "9781591846444"}
        }, random);

        // Finance (25)
        seedGenre(books, "Finance", new String[][]{
            {"Rich Dad Poor Dad", "Robert Kiyosaki", "9781612681139"},
            {"The Psychology of Money", "Morgan Housel", "9780857197680"},
            {"The Intelligent Investor", "Benjamin Graham", "9780060555665"},
            {"The Simple Path to Wealth", "J.L. Collins", "9781533667922"},
            {"I Will Teach You to Be Rich", "Ramit Sethi", "9781523505746"},
            {"The Total Money Makeover", "Dave Ramsey", "9781595555274"},
            {"The Millionaire Next Door", "Thomas Stanley", "9781589795471"},
            {"Your Money or Your Life", "Vicki Robin", "9780143115762"},
            {"A Random Walk Down Wall Street", "Burton Malkiel", "9781324035435"},
            {"The Richest Man in Babylon", "George Clason", "9780451205360"},
            {"Thinking, Fast and Slow", "Daniel Kahneman", "9780374533557"},
            {"Stocks for the Long Run", "Jeremy Siegel", "9780071843669"},
            {"Principles", "Ray Dalio", "9781501124020"},
            {"Common Stocks & Uncommon Profits", "Philip Fisher", "9780471445500"},
            {"One Up On Wall Street", "Peter Lynch", "9780743200516"},
            {"Broke Millennial", "Erin Lowry", "9780143131120"},
            {"The Little Book of Common Sense Investing", "John Bogle", "9781119404507"},
            {"Quit Like a Millionaire", "Kristy Shen", "9780525538691"},
            {"Money: Master the Game", "Tony Robbins", "9781476766270"},
            {"Get Good with Money", "Tiffany Aliche", "9780593232729"},
            {"The Naked Trader", "Robbie Burns", "9780857197178"},
            {"Financial Feminist", "Tori Dunlap", "9780063260269"},
            {"Die With Zero", "Bill Perkins", "9780358099765"},
            {"Investing Demystified", "Lars Kroijer", "9781292156125"},
            {"Value Investing", "James Montier", "9780470683514"}
        }, random);

        // History (25)
        seedGenre(books, "History", new String[][]{
            {"Sapiens", "Yuval Noah Harari", "9780062316097"},
            {"Guns, Germs, and Steel", "Jared Diamond", "9780393317558"},
            {"The Diary of a Young Girl", "Anne Frank", "9780553296983"},
            {"A People's History of the United States", "Howard Zinn", "9780062397348"},
            {"The Wright Brothers", "David McCullough", "9781476728759"},
            {"Killers of the Flower Moon", "David Grann", "9780307742483"},
            {"Team of Rivals", "Doris Kearns Goodwin", "9780684824932"},
            {"The Silk Roads", "Peter Frankopan", "9781101912379"},
            {"1776", "David McCullough", "9780743226721"},
            {"The Guns of August", "Barbara Tuchman", "9780345476098"},
            {"Stalingrad", "Antony Beevor", "9780140284584"},
            {"SPQR", "Mary Beard", "9781631492228"},
            {"Caste", "Isabel Wilkerson", "9780593230251"},
            {"The Splendid and the Vile", "Erik Larson", "9780385347105"},
            {"Genghis Khan and the Making of the Modern World", "Jack Weatherford", "9780609809648"},
            {"Empire of the Summer Moon", "S.C. Gwynne", "9781416591061"},
            {"The Warmth of Other Suns", "Isabel Wilkerson", "9780679763888"},
            {"Bury My Heart at Wounded Knee", "Dee Brown", "9780805066692"},
            {"The Rise and Fall of the Third Reich", "William Shirer", "9781451651683"},
            {"The Wager", "David Grann", "9780385534260"},
            {"Midnight's Children", "Salman Rushdie", "9780143034483"},
            {"The Anarchy", "William Dalrymple", "9781635573954"},
            {"Rubicon", "Tom Holland", "9781400078974"},
            {"Postwar", "Tony Judt", "9780143037750"},
            {"Longitude", "Dava Sobel", "9780007214020"}
        }, random);

        // Science (25)
        seedGenre(books, "Science", new String[][]{
            {"A Brief History of Time", "Stephen Hawking", "9780553380163"},
            {"Cosmos", "Carl Sagan", "9780345331358"},
            {"A Short History of Nearly Everything", "Bill Bryson", "9780767908184"},
            {"The Selfish Gene", "Richard Dawkins", "9780199291151"},
            {"The Immortal Life of Henrietta Lacks", "Rebecca Skloot", "9781400052189"},
            {"Astrophysics for People in a Hurry", "Neil deGrasse Tyson", "9780393609394"},
            {"Silent Spring", "Rachel Carson", "9780618249060"},
            {"Thinking, Fast and Slow", "Daniel Kahneman", "9780374533557"},
            {"The Emperor of All Maladies", "Siddhartha Mukherjee", "9781439107959"},
            {"Behave", "Robert Sapolsky", "9780143110910"},
            {"The Gene", "Siddhartha Mukherjee", "9781476733500"},
            {"The Hidden Life of Trees", "Peter Wohlleben", "9781771642484"},
            {"Unweaving the Rainbow", "Richard Dawkins", "9780618056736"},
            {"The Sixth Extinction", "Elizabeth Kolbert", "9781250062185"},
            {"Chaos", "James Gleick", "9780143113454"},
            {"Lab Girl", "Hope Jahren", "9781101874936"},
            {"Cosmos", "Carl Sagan", "9780345539434"},
            {"Reality Is Not What It Seems", "Carlo Rovelli", "9780735213920"},
            {"Why We Sleep", "Matthew Walker", "9781501144317"},
            {"The Elegant Universe", "Brian Greene", "9780393338102"},
            {"Endurance", "Scott Kelly", "9781524731595"},
            {"Brief Answers to the Big Questions", "Stephen Hawking", "9781984819192"},
            {"A Brief History of Time", "Stephen Hawking", "9780553380163"},
            {"Helgoland", "Carlo Rovelli", "9780593328880"},
            {"An Immense World", "Ed Yong", "9780593133231"}
        }, random);

        // Biography (25)
        seedGenre(books, "Biography", new String[][]{
            {"Steve Jobs", "Walter Isaacson", "9781451648539"},
            {"Becoming", "Michelle Obama", "9781524763138"},
            {"Educated", "Tara Westover", "9780399590504"},
            {"Long Walk to Freedom", "Nelson Mandela", "9780316548182"},
            {"Alexander Hamilton", "Ron Chernow", "9780143034759"},
            {"Einstein: His Life and Universe", "Walter Isaacson", "9780743264747"},
            {"Born a Crime", "Trevor Noah", "9780525509028"},
            {"The Glass Castle", "Jeannette Walls", "9780743247542"},
            {"I Am Malala", "Malala Yousafzai", "9780316322409"},
            {"Wild", "Cheryl Strayed", "9780307476074"},
            {"Greenlights", "Matthew McConaughey", "9780593139134"},
            {"Open", "Andre Agassi", "9780307388407"},
            {"The Autobiography of Malcolm X", "Malcolm X", "9780345350688"},
            {"Leonardo da Vinci", "Walter Isaacson", "9781501139154"},
            {"Shoe Dog", "Phil Knight", "9781501135910"},
            {"I'm Glad My Mom Died", "Jennette McCurdy", "9781982185824"},
            {"Spare", "Prince Harry", "9781039003750"},
            {"Friends, Lovers, and the Big Terrible Thing", "Matthew Perry", "9781250866448"},
            {"Finding Me", "Viola Davis", "9780063037328"},
            {"The Last Lecture", "Randy Pausch", "9781401323257"},
            {"Elon Musk", "Walter Isaacson", "9781982181284"},
            {"Titan", "Ron Chernow", "9780679757030"},
            {"John Adams", "David McCullough", "9780743223133"},
            {"The Power Broker", "Robert Caro", "9780394720241"},
            {"Crying in H Mart", "Michelle Zauner", "9780525657743"}
        }, random);

        // Thriller (25)
        seedGenre(books, "Thriller", new String[][]{
            {"The Da Vinci Code", "Dan Brown", "9780307474278"},
            {"Gone Girl", "Gillian Flynn", "9780307588371"},
            {"The Girl with the Dragon Tattoo", "Stieg Larsson", "9780307454546"},
            {"The Silence of the Lambs", "Thomas Harris", "9780312963965"},
            {"The Silent Patient", "Alex Michaelides", "9781250301697"},
            {"The Girl on the Train", "Paula Hawkins", "9781594634024"},
            {"Shutter Island", "Dennis Lehane", "9780062068477"},
            {"Big Little Lies", "Liane Moriarty", "9780399167065"},
            {"Dark Matter", "Blake Crouch", "9781101904220"},
            {"The Guest List", "Lucy Foley", "9780062868824"},
            {"The Woman in the Window", "A.J. Finn", "9780062678416"},
            {"Verity", "Colleen Hoover", "9781538724545"},
            {"The Housemaid", "Freida McFadden", "9781538742570"},
            {"Local Woman Missing", "Mary Kubica", "9780778311034"},
            {"None of This Is True", "Lisa Jewell", "9781982179090"},
            {"The 7 1/2 Deaths of Evelyn Hardcastle", "Stuart Turton", "9781492657965"},
            {"Thirteen", "Steve Cavanagh", "9781409170679"},
            {"Pretty Girls", "Karin Slaughter", "9780062429070"},
            {"The Chain", "Adrian McKinty", "9781549119330"},
            {"Anatomy of a Scandal", "Sarah Vaughan", "9781501172168"},
            {"Defending Jacob", "William Landay", "9780440246473"},
            {"The Kind Worth Killing", "Peter Swanson", "9780062302434"},
            {"Memory Man", "David Baldacci", "9781455586387"},
            {"The Couple Next Door", "Shari Lapena", "9780735221109"},
            {"Before I Go to Sleep", "S.J. Watson", "9780062060563"}
        }, random);

        // Fantasy (25)
        seedGenre(books, "Fantasy", new String[][]{
            {"The Hobbit", "J.R.R. Tolkien", "9780547928227"},
            {"Harry Potter and the Sorcerer's Stone", "J.K. Rowling", "9780590353427"},
            {"A Game of Thrones", "George R.R. Martin", "9780553593716"},
            {"The Name of the Wind", "Patrick Rothfuss", "9780756404741"},
            {"The Lion, the Witch and the Wardrobe", "C.S. Lewis", "9780064471046"},
            {"Mistborn", "Brandon Sanderson", "9780765350381"},
            {"American Gods", "Neil Gaiman", "9780062080455"},
            {"Circe", "Madeline Miller", "9780316556347"},
            {"Good Omens", "Terry Pratchett", "9780060853969"},
            {"The Fellowship of the Ring", "J.R.R. Tolkien", "9780062731050"},
            {"The Crystal Cave", "Mary Stewart", "9780060548254"},
            {"Eragon", "Christopher Paolini", "9780375826689"},
            {"The Way of Kings", "Brandon Sanderson", "9780765326355"},
            {"The Blade Itself", "Joe Abercrombie", "9781591025337"},
            {"Fourth Wing", "Rebecca Yarros", "9781649374042"},
            {"A Court of Thorns and Roses", "Sarah J. Maas", "9781619634442"},
            {"The House in the Cerulean Sea", "TJ Klune", "9781250254313"},
            {"The Night Circus", "Erin Morgenstern", "9780307744432"},
            {"The Poppy War", "R.F. Kuang", "9780062662569"},
            {"Ninth House", "Leigh Bardugo", "9781250313072"},
            {"Piranesi", "Susanna Clarke", "9781635575637"},
            {"Legends & Lattes", "Travis Baldree", "9781250886088"},
            {"Tress of the Emerald Sea", "Brandon Sanderson", "9781250899651"},
            {"Babel", "R.F. Kuang", "9780063021426"},
            {"The Starless Sea", "Erin Morgenstern", "9780385541213"},
            {"Stardust", "Neil Gaiman", "9780062564344"}
        }, random);

        // Romance (25)
        seedGenre(books, "Romance", new String[][]{
            {"Pride and Prejudice", "Jane Austen", "9780141439518"},
            {"The Notebook", "Nicholas Sparks", "9781455582815"},
            {"It Ends with Us", "Colleen Hoover", "9781501110368"},
            {"The Hating Game", "Sally Thorne", "9780062439598"},
            {"Red, White & Royal Blue", "Casey McQuiston", "9781250243867"},
            {"Outlander", "Diana Gabaldon", "9780440212560"},
            {"Beach Read", "Emily Henry", "9781984806734"},
            {"The Kiss Quotient", "Helen Hoang", "9780451490803"},
            {"Me Before You", "Jojo Moyes", "9780143124542"},
            {"Book Lovers", "Emily Henry", "9780593334836"},
            {"People We Meet on Vacation", "Emily Henry", "9781984806758"},
            {"Ugly Love", "Colleen Hoover", "9781476753188"},
            {"Happy Place", "Emily Henry", "9780593441275"},
            {"The Seven Husbands of Evelyn Hugo", "Taylor Jenkins Reid", "9781501139239"},
            {"Bridgerton: The Duke and I", "Julia Quinn", "9780062353597"},
            {"Normal People", "Sally Rooney", "9781984822178"},
            {"Twilight", "Stephenie Meyer", "9780316015844"},
            {"A Walk to Remember", "Nicholas Sparks", "9780446608954"},
            {"Better than the Movies", "Lynn Painter", "9781534467637"},
            {"The Spanish Love Deception", "Elena Armas", "9781665914444"},
            {"Verity", "Colleen Hoover", "9781538724545"},
            {"Love, Theoretically", "Ali Hazelwood", "9780593549834"},
            {"The Love Hypothesis", "Ali Hazelwood", "9780593336823"},
            {"Reminders of Him", "Colleen Hoover", "9781542025607"},
            {"Part of Your World", "Abby Jimenez", "9781538704332"}
        }, random);

        // Productivity (25)
        seedGenre(books, "Productivity", new String[][]{
            {"Getting Things Done", "David Allen", "9780143126560"},
            {"Deep Work", "Cal Newport", "9781455586691"},
            {"Eat That Frog!", "Brian Tracy", "9781626569416"},
            {"The 4-Hour Workweek", "Timothy Ferriss", "9780307465351"},
            {"Essentialism", "Greg McKeown", "9780804137386"},
            {"The Power of Habit", "Charles Duhigg", "9780812981605"},
            {"Smarter Faster Better", "Charles Duhigg", "9780812983593"},
            {"Flow", "Mihaly Csikszentmihalyi", "9780061339202"},
            {"Building a Second Brain", "Tiago Forte", "9781982171681"},
            {"The 5 AM Club", "Robin Sharma", "9781443456623"},
            {"The ONE Thing", "Gary Keller", "9781885167774"},
            {"Hyperfocus", "Chris Bailey", "9780525522232"},
            {"Miracle Morning", "Hal Elrod", "9780985666729"},
            {"168 Hours", "Laura Vanderkam", "9781591844105"},
            {"Effortless", "Greg McKeown", "9780593135648"},
            {"Feel-Good Productivity", "Ali Abdaal", "9781250325600"},
            {"Show Your Work!", "Austin Kleon", "9780761178972"},
            {"Make Time", "Jake Knapp", "9780593079584"},
            {"Scrum", "Jeff Sutherland", "9780385346450"},
            {"Work Clean", "Dan Charnas", "9781623363567"},
            {"The checklist Manifesto", "Atul Gawande", "9780805091748"},
            {"So Good They Can't Ignore You", "Cal Newport", "9781455509126"},
            {"Refuse to Choose!", "Barbara Sher", "9781594866265"},
            {"The Productivity Project", "Chris Bailey", "9781101904052"},
            {"Rest", "Alex Soojung-Kim Pang", "9780465074877"}
        }, random);

        List<Book> savedBooks = bookRepository.saveAll(books);
        seedReviews(savedBooks, random);
        logger.info("Successfully seeded 250 popular books and reviews.");
    }

    private static final List<String> COLORS = Arrays.asList(
        "#fef3f2", "#f0f9ff", "#f0fdf4", "#fffbeb", "#faf5ff", "#fdf2f8", "#fff7ed"
    );

    private static final List<String> EMOJIS = Arrays.asList(
        "📚", "📖", "🖋️", "📝", "🎨", "🏛️", "🧪", "💡", "🌍", "🧠", "✨", "📜", "🔖"
    );

    private void seedReviews(List<Book> savedBooks, Random random) {
        String[] usernames = {"TheDailyReader", "LiteraryLighthouse", "NovelNerd", "BookishBreeze", "ChapterChatter", "InkAndIrony", "PageProse", "VelvetVolume", "MidnightManuscript", "GoldenGlosses", "EchoesOfEverest", "ProseAndPassion", "TheBibliophile", "SilentSolace", "NarrativeNavigator"};
        
        String[][] reviewPool = {
            {"A profound exploration of the human condition. The prose is lyrical and the themes are universal. It reminded me of why I fell in love with reading in the first place.", "5"},
            {"This book changed how I look at the world. The author has a unique gift for making the complex feel simple and the mundane feel magical.", "5"},
            {"An edge-of-your-seat thriller with twists I never saw coming. I finished it in one sitting and still couldn't sleep!", "5"},
            {"A beautifully woven tapestry of history and emotion. You can tell how much research went into every single paragraph. A triumph of historical fiction.", "5"},
            {"The character development is where this book truly shines. I felt like I knew them personally by the end of the first chapter.", "5"},
            {"A bit slow in the second act, but the payoff in the finale is absolutely worth the wait. The emotional resonance is palpable.", "4"},
            {"While the premise was interesting, the execution felt a bit dated. Still a solid read for fans of the genre who enjoy classic tropes.", "3"},
            {"I struggled with the pacing, but the world-building is second to none. Truly imaginative and deeply atmospheric.", "4"},
            {"A refreshing take on a familiar trope. The dialogue is snappy, the pacing is relentless, and the stakes feel genuinely high.", "5"},
            {"Masterful storytelling. It's rare to find a book that balances plot and philosophy so perfectly without feeling preachy.", "5"},
            {"An absolute classic that remains incredibly relevant today. A must-read for any serious reader looking for depth and substance.", "5"},
            {"The ending left me speechless. It's the kind of book you immediately want to discuss with others. I can't stop thinking about it.", "5"},
            {"Thought-provoking and courageous. The author doesn't shy away from difficult questions and avoids easy answers.", "4"},
            {"A cozy, heartwarming read that felt like a warm hug. Perfect for a rainy afternoon with a cup of tea.", "4"},
            {"The first half was brilliant, but I felt the ending was a bit rushed. Overall, still highly recommended for the journey alone.", "3"},
            {"Gripping from the very first page. The tension never lets up until the very end. A masterclass in suspense.", "5"},
            {"The prose is so dense it's almost poetic. You have to read it slowly to really appreciate the craft.", "4"},
            {"I didn't expect to cry, but the final chapter destroyed me. Such a beautiful, tragic story.", "5"},
            {"Highly informative and surprisingly accessible. Even if you aren't into the subject, you'll find it fascinating.", "4"},
            {"A bold, experimental work that won't be for everyone, but those who 'get it' will absolutely adore it.", "4"}
        };

        int totalSaved = 0;
        // Seed reviews for ALL books
        for (Book book : savedBooks) {
            int numReviews = random.nextInt(5) + 3; // 3 to 7 reviews per book
            
            double totalRating = 0;
            for (int j = 0; j < numReviews; j++) {
                String[] reviewData = reviewPool[random.nextInt(reviewPool.length)];
                int rating = Integer.parseInt(reviewData[1]);
                totalRating += rating;
                
                Review r = Review.builder()
                    .bookId(book.getId())
                    .userId("user-" + random.nextInt(5000))
                    .username(usernames[random.nextInt(usernames.length)])
                    .rating(rating)
                    .reviewText(reviewData[0])
                    .createdAt(LocalDateTime.now().minusDays(random.nextInt(365)))
                    .build();
                
                reviewRepository.save(r);
                totalSaved++;
            }
            
            book.setAverageRating(totalRating / numReviews);
            book.setReviewCount(numReviews);
        }
        
        bookRepository.saveAll(savedBooks);
        long finalReviewCount = reviewRepository.count();
        logger.info("Successfully seeded {} books and {} reviews. Total reviews in DB: {}", savedBooks.size(), totalSaved, finalReviewCount);
    }

    private void seedGenre(List<Book> books, String genre, String[][] data, Random random) {
        for (String[] item : data) {
            String title = item[0];
            String author = item[1];
            String isbn = item[2];
            
            String type = (random.nextDouble() > 0.85) ? "DONATE" : "SELL";
            double price = (type.equals("DONATE")) ? 0.0 : 199 + random.nextInt(600);
            
            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setCategory(genre);
            book.setPrice(price);
            book.setType(type);
            book.setCondition(getRandomCondition(random));
            book.setDescription("A premium edition of " + title + " by " + author + ". An essential work for your " + genre + " collection.");
            book.setImageUrl("https://covers.openlibrary.org/b/isbn/" + isbn + "-L.jpg");
            book.setAvailable(true);
            book.setListedAt(LocalDateTime.now().minusDays(random.nextInt(60)));
            book.setUpdatedAt(LocalDateTime.now());
            
            // New fields for Goodreads-style review section
            book.setAverageRating(3.5 + (random.nextDouble() * 1.5)); // Random rating between 3.5 and 5.0
            book.setReviewCount(random.nextInt(50) + 5);
            book.setCoverColor(COLORS.get(random.nextInt(COLORS.size())));
            book.setEmoji(EMOJIS.get(random.nextInt(EMOJIS.size())));
            
            books.add(book);
        }
    }

    private String getRandomCondition(Random random) {
        String[] conditions = {"Like New", "Good", "Fair"};
        return conditions[random.nextInt(conditions.length)];
    }
}
