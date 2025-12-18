package bootcamp.kakao.server.controller;

import bootcamp.kakao.server.common.dto.DataResponseDto;
import bootcamp.kakao.server.common.enums.Code;
import bootcamp.kakao.server.dto.home.HomeResponseDto;
import bootcamp.kakao.server.dto.learningsource.ProgressResponseDto;
import bootcamp.kakao.server.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/{userId}")
    public DataResponseDto<HomeResponseDto> getHomeData(@PathVariable ("userId") Long userId) {
        HomeResponseDto homeResponseDto = homeService.getHomeData(userId);
        return new DataResponseDto<>(Code.OK, "홈 데이터를 성공적으로 조회하였습니다.", homeResponseDto);
    }

    @GetMapping("/{userId}/progress")
    public DataResponseDto<ProgressResponseDto> getOverallProgress(@PathVariable ("userId") Long userId) {
        ProgressResponseDto progressResponseDto = homeService.getOverallProgress(userId);
        return new DataResponseDto<ProgressResponseDto>(Code.OK, "전체 진도율을 성공적으로 조회하였습니다.", progressResponseDto);
    }
}
