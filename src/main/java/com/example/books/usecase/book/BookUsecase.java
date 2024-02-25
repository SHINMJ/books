package com.example.books.usecase.book;

import com.example.books.domain.book.Book;
import com.example.books.domain.book.BookRepository;
import com.example.books.domain.member.Member;
import com.example.books.usecase.auth.dto.LoginUser;
import com.example.books.usecase.book.dto.BookResponse;
import com.example.books.usecase.book.dto.BorrowedRequest;
import com.example.books.usecase.book.dto.ConsignmentRequest;
import com.example.books.usecase.book.dto.ReturnedRequest;
import com.example.books.usecase.member.MemberUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Transactional
@Service
public class BookUsecase {
    private static final int RETURN_TIME = 10;

    private final BookRepository repository;
    private final MemberUsecase memberUsecase;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public BookResponse consignment(ConsignmentRequest request, LoginUser loginUser){

        Member owner = memberUsecase.findById(loginUser.getId());

        Book saved = repository.save(request.toEntity(owner));

        return BookResponse.of(saved);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> findPageAllBy(Pageable pageable) {
        Page<Book> books = repository.findAllBy(pageable);
        return books.map(BookResponse::of);
    }

    public void borrowed(BorrowedRequest request, LoginUser loginUser){
        List<Book> books = repository.findAllById(request.ids());

        List<Book> borrowedBooks = books.stream()
                .map(Book::borrowed)
                .toList();

        List<Book> saved = repository.saveAll(borrowedBooks);

        /**
         * @Todo
         * 이벤트로 처리
         */
        memberUsecase.addBorrowed(loginUser, saved);

        // 10초 뒤 반납 처리
        scheduler.schedule(() -> {
            returned(new ReturnedRequest(request.ids()), loginUser);
        }, RETURN_TIME, TimeUnit.SECONDS);
    }

    public void returned(ReturnedRequest request, LoginUser loginUser){
        List<Book> books = repository.findAllById(request.ids());

        List<Book> returnedBooks = books.stream()
                .map(Book::returned)
                .toList();

        repository.saveAll(returnedBooks);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> myBookList(Pageable pageable, LoginUser loginUser) {
        Member owner = memberUsecase.findById(loginUser.getId());
        Page<Book> books = repository.findAllByOwner(owner, pageable);
        return books.map(BookResponse::of);
    }

}
