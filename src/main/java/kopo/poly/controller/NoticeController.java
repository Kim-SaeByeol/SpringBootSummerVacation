package kopo.poly.controller;

import kopo.poly.dto.NoticeDTO;
import kopo.poly.service.INoticeService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
public class NoticeController {
    //@RequiredArgsConstructor를 통해 메모리에 올라간 서비스 객체를 Controller에서 사용할 수 있게 주입
    private final INoticeService noticeService;

    /*
     * 게시판 리스트 보여주기
     *  GetMaipping(value = "notice/noticeList") => Get 방식을 통해 접속되는 URL이 notice/noticeList 경우 아래 함수로 전송
     */

    @GetMapping(value = "/notice/noticeList")
    public String noticeList(ModelMap model) throws Exception {

        //로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이
        log.info(this.getClass().getName() + ".noticeList start!");

        //공지사항 리스트 조회하기
        List<NoticeDTO> rList = noticeService.getNoticeList();
        if (rList == null)
            rList = new ArrayList<>();
        //JAVA 8부터 제공되는 Optional 활욯아여 NPE(Null Pointer Exception) 처리
        //List<NoticeDTO> rList = Optional.ofNullable(noticeService.getNoticeList()).orElseGet(ArrayList : new);

        //조회된 리스트 결과값 넣어주기
        model.addAttribute("rList", rList);

        //로그 찍기(추후 찍은 로그를 통해 이 함수호출이 끝났는지 파악하기 용이
        log.info(this.getClass().getName() + ".noticeList End!");

        // 함수 처리가 끝나고 보여줄 html 파일명
        return "/notice/noticeList";
    }

    @PatchMapping(value = "/notice/noticeInsert")
    public String noticeInsert(HttpServletRequest request, ModelMap model, HttpSession session) {
        log.info(this.getClass().getName() + ".noticeInsert Start");

        String msg = "";    //메세지 내용
        String url = "/notice/noticeReg";   //이동할 경로 내용

        try {
            //로그인 할 사용자 아이디를 가져오기
            // 로그인을 아직 구현하지 않았기에 공지사항 리스트에서 로그인 한 것 처럼 Session 값을 지정
            String user_id = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String title = CmmUtil.nvl(request.getParameter("title"));   //제목
            String notice_yn = CmmUtil.nvl(request.getParameter("notice_yn"));  // 공지글 내부
            String contents = CmmUtil.nvl(request.getParameter("contents"));    //내용

            /*
             * ##################################################################################
             * 반드시, 값을 받았으면 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함. 반드시 작성할 것.
             * ##################################################################################
             */
            log.info("session user_id : " + user_id);
            log.info("title : " + title);
            log.info("notice_yn : " + notice_yn);
            log.info("contents : " + contents);

            // 데이터 저장하기 위해 DTO 에 저장하기
            NoticeDTO pDTO = new NoticeDTO();
            pDTO.setUser_id(user_id);
            pDTO.setTitle(title);
            pDTO.setNotice_yn(notice_yn);
            pDTO.setContents(contents);

            /*
             * 게시글 등록하기 위한 비즈니스 로직을 호출
             */
            noticeService.inserNoticeInfo(pDTO);

            // 저장이 완료되면 사용자에게 보여줄 메세지
            msg = "등록되었습니다.";
            url = "/notice/noticeList";

        } catch (Exception e) {
            // 저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            //결과 메시지 전달하기
            model.addAttribute("msg", msg);
            model.addAttribute("url", url);
            log.info(this.getClass().getName() + ".noticeInsert End!");
        }
        return "/redirect";
    }

    @GetMapping(value = "/notice/noticeInfo")
    public String noticeInfo(HttpServletRequest request, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ",noticeInfo Start");
        String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));

        /*
         * ############################################################
         *   반드시, 값을 받았으면 꼭 로그를 찍어서 값이 제대로 들어왔는지 확인하자.
         * ############################################################
         */
        log.info("nSeq : " + nSeq);

        /*
         * 값 전달은 반드시 DTO 객체를 이용해서 처리함. 전달 받은 값을 DTO 객체에 넣는다.
         */

        NoticeDTO pDTO = new NoticeDTO();
        pDTO.setNotice_seq(nSeq);

        // 공지사항 상세정보 가져오기
        // JAVA 8 부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        NoticeDTO rDTO = Optional.ofNullable(noticeService.getNoticeInfo(pDTO, true)).orElseGet(NoticeDTO::new);

        //조회된 리스트 결과값 넣어주기
        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".noticeInfo End!");

        // 함수 처리가 끝나고 보여줄 html 파일명
        return "/notice/noticeInfo";
    }

    public String noticeEditInfo(HttpServletRequest request, ModelMap model) throws Exception {
        log.info((this.getClass().getName() + ".noticeEdiInfo Start!"));
        String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));

        log.info("nSeq : " + nSeq);

        NoticeDTO pDTO = new NoticeDTO();
        pDTO.setNotice_seq(nSeq);

        NoticeDTO rDTO = noticeService.getNoticeInfo(pDTO, false);
        if(rDTO == null) rDTO = new NoticeDTO();
        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        // Notice DTO rDTO = Optional.ofNullable(noticeService.getNoticeInfo(pDTO, false)).orElseGet(NoticeDTO ::new);

        //조회된 리스트 결과 값을 넣어주기
        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".noticeEditInfo End");

        // 함수 처리가 끝나고 보여줄 html 파일명
        return "/notice/noticeEditInfo";
    }
        /*
        *   게시판 글 수정 실행 로직
         */
    @PostMapping(value = "/notice/noticeUpdate")
    public String noticeUpdate(HttpSession session, ModelMap model, HttpServletRequest request) throws Exception{
        log.info(this.getClass().getName() + ".noticeUpdate Start!");

        String msg = "";    //메시지 내용
        String url = "/notice/noticeInfo";  //이동할 경로

        try {
            String user_id = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));  // 아이디
            String nseq = CmmUtil.nvl(request.getParameter("nSeq"));    // 글번호(PK)
            String title = CmmUtil.nvl(request.getParameter("title"));  // 제목
            String notice_yn = CmmUtil.nvl(request.getParameter("notice_yn"));  // 공지글 여부
            String contents = CmmUtil.nvl(request.getParameter("contents"));  // 내용

            log.info("user_id : " + user_id);
            log.info("nSeq : " + nseq);
            log.info("title : " + title);
            log.info("notice_yn : " + notice_yn);
            log.info("contents : " + contents);

            /*
            * 값 전달은 반드시 DTO 객체를 이용해서 처리함. 전달 받은 값을 DTO 객체에 넣음.
             */

            NoticeDTO pDTO = new NoticeDTO();
            pDTO.setUser_id(user_id);
            pDTO.setNotice_seq(nseq);
            pDTO.setTitle(title);
            pDTO.setNotice_yn(notice_yn);
            pDTO.setContents(contents);

            // 게시글 수정하기 DB
            noticeService.updateNoticeInfo(pDTO);

            msg = "수정되었습니다.";

        }catch (Exception e){
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            model.addAttribute("msg", msg);
            model.addAttribute("url", url);
            log.info(this.getClass().getName() + ".noticeUpdate End");
        }
        return "/redirect";
    }

}
