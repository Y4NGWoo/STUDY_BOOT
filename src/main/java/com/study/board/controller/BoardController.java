package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    //글 작성 폼
    @GetMapping("/writeFrom") // localhost:9999/board/write
    public String boardWriteForm(){

        return "boardwrite";
    }

    // 글 작성 등록
    @PostMapping("/register")
    public String boardRegist(Board board, Model model, MultipartFile file) throws Exception{

        boardService.boardWrite(board, file);
        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");
        return "message";
    }

    // 글 수정 폼
    @GetMapping("/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model){

        model.addAttribute("board", boardService.boardDetail(id));
        return "boardmodify";
    }

    // 글 수정 등록
    @PostMapping("/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, MultipartFile file) throws Exception{

        Board boardTemp = boardService.boardDetail(id); // 기존에 있는 글 담기
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContents(board.getContents());
        boardTemp.setWriter(board.getWriter());

        boardService.boardWrite(boardTemp, file);
        model.addAttribute("message", "글 수정이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");
        return "message";
    }

    // 게시글 리스트 처리
    @GetMapping("/list")
    public String boardList(Model model, String searchKeyword, String searchCondition
         ,@PageableDefault(page=0, size=10, sort="id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Board> list = null;

        if(searchKeyword == null){
            list = boardService.boardList(pageable);
        } else if(searchCondition.equals("title")){
            list = boardService.boardSearchListByTitle(searchKeyword ,pageable);
        } else if(searchCondition.equals("writer")){
            list = boardService.boardSearchListByWriter(searchKeyword ,pageable);
        } else if(searchCondition.equals("contents")){
            list = boardService.boardSearchListByContents(searchKeyword ,pageable);
        } else if(searchCondition.equals("all")){
            list = boardService.boardSearchListByAll(searchKeyword ,pageable);
        }

        int nowPage = list.getPageable().getPageNumber() + 1; //pageable.getPageNumber(); 도 가능 0에서 시작하기때문에 1 더해줌
        int startPage = Math.max(nowPage - 4, 1);       // 둘중에 큰값 반환
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage" , nowPage);
        model.addAttribute("startPage" , startPage);
        model.addAttribute("endPage" , endPage);
        return "boardlist";
    }

    // 특정 게시글 처리
    @GetMapping("/detail")
    public String boardDetail(Model model, Integer id) {

        model.addAttribute("board", boardService.boardDetail(id));
        return "boarddetail";
    }

    @GetMapping("/delete")
    public String boardDelete(Integer id){

        boardService.boardDelete(id);
        return "redirect:/board/list";
    }

}
