package org.example.BookStoreApplication.service;

import lombok.RequiredArgsConstructor;
import org.example.BookStoreApplication.dto.BookDto;
import org.example.BookStoreApplication.dto.PageResponse;
import org.example.BookStoreApplication.entity.Book;
import org.example.BookStoreApplication.repository.BookRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookService {

    private final ModelMapper modelMapper;
    private final BookRepository bookRepository;

    public BookDto createBook(BookDto bookDto){
        Book book = modelMapper.map(bookDto, Book.class);
        Book saved = bookRepository.save(book);
        return modelMapper.map(saved, BookDto.class);
    }

    public BookDto updateBookById(Long id, BookDto bookDto){
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book id not found"));

        existingBook.setTitle(bookDto.getTitle());
        existingBook.setAuthor(bookDto.getAuthor());
        existingBook.setPrice(bookDto.getPrice());

        Book updated = bookRepository.save(existingBook);
        return modelMapper.map(updated, BookDto.class);
    }

    public BookDto getBookById(Long id){
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return modelMapper.map(book, BookDto.class);
    }

    public BookDto deleteById(Long id){
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        bookRepository.deleteById(id);
        return modelMapper.map(book, BookDto.class);
    }

    public List<BookDto> getAllBooks(){
        return bookRepository.findAll()
                .stream()
                .map(book -> modelMapper.map(book, BookDto.class))
                .toList();
    }

    // Pagination + Sorting
    public PageResponse<BookDto> getBooks(int page, int size, String sortBy, String direction){

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Book> bookPage = bookRepository.findAll(pageable);

        List<BookDto> dtoList = bookPage.getContent()
                .stream()
                .map(book -> modelMapper.map(book, BookDto.class))
                .toList();

        return new PageResponse<>(
                dtoList,
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages()
        );
    }
}