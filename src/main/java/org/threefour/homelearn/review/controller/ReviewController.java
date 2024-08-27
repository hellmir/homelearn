package org.threefour.homelearn.review.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.threefour.homelearn.member.dto.MemberResponseDTO;
import org.threefour.homelearn.member.jwt.JWTUtil;
import org.threefour.homelearn.member.mapper.MemberMapper;
import org.threefour.homelearn.review.domain.Review;
import org.threefour.homelearn.review.service.ReviewService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ReviewController {

  @Autowired
  private MemberMapper memberMapper;

  @Autowired
  private ReviewService reviewService;


  @Autowired
  private JWTUtil jwtUtil;

  @PostMapping("/writeReview")
  public @ResponseBody List<Review> writeReview(String content, String url) {
    ModelAndView view = new ModelAndView();
    MemberResponseDTO memberDTO = memberMapper.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

    int index = url.lastIndexOf("=");
    String strCourse_id = url.substring(index + 1, url.length());

    long member_id = memberDTO.getId();
    long course_id = 0L;
    if (strCourse_id != null) {
      course_id = Long.parseLong(strCourse_id);
    }


    Review review = new Review();
    review.setStudent_id(member_id);
    review.setCourse_id(course_id);
    review.setContent(content);
    review.setScore(10);
    review.setCreated_at(LocalDateTime.now());
    review.setModified_at(LocalDateTime.now());
    reviewService.writeReview(review);
    List<MemberResponseDTO> getMemberResponseDTOList = memberMapper.selectReview(course_id);

    int i = 0;
    List<Review> reviews = getList2(course_id);
    for (i = 0; i < reviews.size() - 1; i++) {

      String createdAt = getMemberResponseDTOList.get(i).getCreatedAt();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      LocalDateTime dateTime = LocalDateTime.parse(createdAt, formatter);
      reviews.get(i).setCreated_at(dateTime);

      reviews.get(i).setMember_nickName(getMemberResponseDTOList.get(i).getNickname());

    }

    System.out.println("createat :   " + reviews.get(0).getCreated_at());
    System.out.println("reviews :   " + reviews.get(0).getCreated_at());
    System.out.println("reviews :   " + reviews.get(0).getModified_at());
    return reviews;
  }

  @ResponseBody
  @PostMapping("/reviewList")
  public List<Review> getList(String url) {
    System.out.println();

    System.out.println("getList(String url)1 실행됨" + url);
    System.out.println();

    ModelAndView view = new ModelAndView();
    int index = url.lastIndexOf("=");
    String strCourse_id = url.substring(index + 1, url.length());
    long course_id = 0L;
    if (strCourse_id != null) {
      course_id = Long.parseLong(strCourse_id);
    }

    List<Review> reviews = reviewService.getList(course_id);

    return reviews;
  }
  public List<Review> getList2(long course_id){
    System.out.println();

    System.out.println("getList(String url)2 실행됨"+course_id);
    System.out.println();

    ModelAndView view = new ModelAndView();


    List<Review> reviews = reviewService.getList(course_id);

    return reviews;
  }
}
