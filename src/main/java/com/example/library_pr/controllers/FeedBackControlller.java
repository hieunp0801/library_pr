package com.example.library_pr.controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.library_pr.models.Book;
import com.example.library_pr.models.Feedback;
import com.example.library_pr.models.ResponseObject;
import com.example.library_pr.models.User;
import com.example.library_pr.repositories.BookRepository;
import com.example.library_pr.repositories.FeedbackRepository;
import com.example.library_pr.repositories.UserRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class FeedBackControlller {
	 @Autowired
	  private BookRepository bookRepository;
	  @Autowired
	  private FeedbackRepository feedbackRepository;
	  @Autowired
	  private UserRepository userRepository;
	  @GetMapping("/book/{bookId}/comments")
	  public ResponseEntity<ResponseObject> getAllCommentsByBookId(@PathVariable Long bookId) {
	    if (!bookRepository.existsById(bookId)) {
	    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
	                new ResponseObject("Not found", "failed", "")
	        );
	    }

	    List<Feedback> ratings = feedbackRepository.findByBookId(bookId);
	    return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "successfully", ratings)

        );
	  }
	  @PostMapping("/book/{bookId}/comments")
	  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	  public ResponseEntity<ResponseObject> createFeedback(@PathVariable Long bookId,
			  @RequestParam String comment,
			  @RequestParam float star
			  ){
		  
		  try {
			  Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			  Optional<User> optionUser = userRepository.findByUsername(auth.getName());
			  Book book = bookRepository.findById(bookId).get();
			  User user = optionUser.get();
			  Date date = new Date();
			  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			  Feedback rating  = new Feedback();
			  rating.setBook(book);
			  rating.setUser(user);
			  rating.setComment(comment);
			  rating.setStar(star);
			  rating.setDatetime(sdf.format(date.getTime()));
			  return ResponseEntity.status(HttpStatus.OK).body(
		                new ResponseObject("ok", "successfully",feedbackRepository.save(rating)));
		  }
		  catch(Exception e) {
			  return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
		                new ResponseObject("ok", "Fail",e.getMessage()));
		  }
	  }  
}
