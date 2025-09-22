package com.example.spring_ai.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class ImageAnalysisService {

    private static final List<String> RELATED_KEYWORDS = Arrays.asList(
        "ho chi minh", "bác hồ", "chủ tịch hồ chí minh", "uncle ho",
        "nhà sàn", "bác hồ làm việc", "lăng bác", "bảo tàng hồ chí minh",
        "khu di tích", "phủ chủ tịch", "làng sen", "kim liên",
        "cách mạng", "độc lập", "giải phóng", "kháng chiến",
        "hcm", "bac ho", "chu tich", "di chuc"
    );

    public boolean isImageRelatedToHoChiMinh(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String filename = file.getOriginalFilename().toLowerCase();
        if (filename == null) {
            return false;
        }


        for (String keyword : RELATED_KEYWORDS) {
            if (filename.contains(keyword.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
