package com.example.communityProject.service;

import com.example.communityProject.entity.Image;
import com.example.communityProject.repository.ImageRepository;
import com.example.communityProject.repository.PostRepository;
import com.example.communityProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Value("${file.upload-dir}") // application.properties에서 설정한 경로
    String uploadDir;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    // 이미지 저장
    @Transactional
    public Image saveImage(MultipartFile file) throws IOException {
        // 저장할 디렉토리 생성 (없으면 생성)
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 파일 저장 경로 설정
        String originalName = file.getOriginalFilename();
        if (originalName.length() > 50) {
            originalName = originalName.substring(0, 50); // 최대 50자로 잘라냄
        }
        String fileName = System.currentTimeMillis() + "_" + originalName;
        Path filePath = Paths.get(uploadDir, fileName);
        Files.write(filePath, file.getBytes());

        // DB에 파일 정보 저장
        Image image = new Image(fileName, filePath.toString());
        return imageRepository.save(image);
    }


    @Transactional
    public void deleteImage(Long imageId) {
        imageRepository.deleteById(imageId);
    }


    // 게시글 이미지 조회 (ID로 검색)
    public List<Image> getImageByPostId(Long id) {
        return imageRepository.findByPost_Id(id);
    }


}

