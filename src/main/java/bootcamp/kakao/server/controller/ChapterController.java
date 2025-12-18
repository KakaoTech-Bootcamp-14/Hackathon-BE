package bootcamp.kakao.server.controller;

import bootcamp.kakao.server.common.dto.DataResponseDto;
import bootcamp.kakao.server.common.dto.ResponseDto;
import bootcamp.kakao.server.common.enums.Code;
import bootcamp.kakao.server.controller.swagger.ChapterControllerSpec;
import bootcamp.kakao.server.dto.learningsource.ProgressResponseDto;
import bootcamp.kakao.server.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController implements ChapterControllerSpec {

    private final ChapterService chapterService;

    @GetMapping("/{chapterId}/progress")
    public DataResponseDto<ProgressResponseDto> getChapterProgress(@PathVariable("chapterId") Long chapterId) {
        ProgressResponseDto progressResponseDto = chapterService.getChapterProgress(chapterId);
        return new DataResponseDto<>(Code.OK, "챕터의 진도율을 성공적으로 조회하였습니다.", progressResponseDto);
    }

    @PatchMapping("/{chapterId}/complete-all")
    public ResponseDto completeAllTasksByChapter(@PathVariable("chapterId") Long chapterId) {
        chapterService.completeAllTasksByChapterId(chapterId);
        return new ResponseDto(Code.OK.getCode(), "챕터의 모든 학습 Task가 성공적으로 완료 처리되었습니다.");
    }
}
