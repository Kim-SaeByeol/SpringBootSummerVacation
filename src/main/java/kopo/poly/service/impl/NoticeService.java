package kopo.poly.service.impl;

import kopo.poly.dto.NoticeDTO;
import kopo.poly.persistance.mapper.INoticeMapper;
import kopo.poly.service.INoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeService implements INoticeService {
    // RequiredArgsConstructor 이노테이션으로 생성자를 자동 생성
    // NoticeMapper 변수에 이미 메모리에 올라간 INoticeMapper 객체를 넣어줌
    // 예전에는 autowired 이노테이션을 통해 설정하였지만, 이젠 생성자를 통해 겍체를 주입함.

    private final INoticeMapper noticeMapper;

    @Override
    public List<NoticeDTO> getNoticeList() throws Exception {
            log.info(this.getClass().getName() + "getNoticeList start!");

        return noticeMapper.getNoticeList();
    }

    @Transactional
    @Override
    public NoticeDTO getNoticeInfo(NoticeDTO pDTO, boolean type) throws Exception {
        log.info(this.getClass().getName() + ".getNoticeInfo start!");

        //상세보기 할 때마다 조회수 증가하기(수정보기 제외)
        if(type){
            log.info("Update ReadCNT");
            noticeMapper.updateNoticeReadCnt(pDTO);
        }
        return noticeMapper.getNoticeInfo(pDTO);
    }

    @Transactional
    @Override
    public void inserNoticeInfo(NoticeDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".InserNoticeInfo start!");
        noticeMapper.inserNoticeInfo(pDTO);
    }

    @Transactional
    @Override
    public void updateNoticeInfo(NoticeDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + "updateNoticeInfo start!");
        noticeMapper.updateNoticeInfo(pDTO);
    }
}
