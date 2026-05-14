package vn.edu.uit.devorbit_api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Map<?, ?> upload(MultipartFile file) {
        if (cloudinary == null) {
            // No Cloudinary configured — return a basic response
            return Map.of("url", "");
        }
        try {
            Map<?, ?> data = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return data;
        } catch (IOException io) {
            throw new RuntimeException("Image upload fail");
        }
    }
}
