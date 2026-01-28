package pl.rentathing.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.rentathing.item.dto.ItemAdminListDTO;
import pl.rentathing.item.dto.ItemDetailsDTO;
import pl.rentathing.item.dto.ItemFormDTO;
import pl.rentathing.item.dto.ItemSearchCriteria;
import pl.rentathing.item.entity.Category;
import pl.rentathing.item.entity.Item;
import pl.rentathing.item.exception.ItemNotFoundException;
import pl.rentathing.item.mapper.ItemMapper;
import pl.rentathing.item.repository.CategoryRepository;
import pl.rentathing.item.repository.ItemRepository;
import pl.rentathing.item.repository.ItemSpecifications;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final CategoryRepository categoryRepository;

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id.toString()));
    }

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Transactional
    public void saveItem(ItemFormDTO itemDto) {
        Item item;

        if (itemDto.getId() != null) {
            item = itemRepository.findById(itemDto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Błąd ID: " + itemDto.getId()));
        } else {
            item = new Item();
            item.setAverageRating(0.0);
            item.setReviewCount(0);
        }

        itemMapper.updateEntityFromDto(itemDto, item);

        Category category = categoryRepository.findById(itemDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Błąd kategorii"));
        item.setCategory(category);

        if (itemDto.getImageFile() != null && !itemDto.getImageFile().isEmpty()) {
            try {
                String imagePath = saveImage(itemDto.getImageFile());
                item.setImageUrl(imagePath);
            } catch (IOException e) {
                throw new RuntimeException("Nie udało się zapisać zdjęcia", e);
            }
        }

        itemRepository.save(item);
    }

    public ItemFormDTO getItemFormById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ID: " + id));
        return itemMapper.toDto(item);
    }

    private String saveImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + fileName;
    }

    public ItemDetailsDTO getItemDetails(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id.toString()));
        ItemDetailsDTO dto = itemMapper.toDetailsDTO(item);
        dto.setDiscountedPrice(calculateDiscountedPrice(item));
        return dto;
    }

    private BigDecimal calculateDiscountedPrice(Item item) {
        if (item.getDiscountedPercent() == null || item.getDiscountedPercent() <= 0) {
            return item.getPricePerDay();
        }
        return item.getPricePerDay().subtract(
                item.getPricePerDay().multiply(BigDecimal.valueOf(item.getDiscountedPercent()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
        );
    }

    public Page<ItemAdminListDTO> getAdminItemsPage(ItemSearchCriteria criteria, Pageable pageable) {
        Specification<Item> spec = ItemSpecifications.build(criteria);

        return itemRepository.findAll(spec, pageable)
                .map(item -> ItemAdminListDTO.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .description(item.getDescription())
                        .categoryName(item.getCategory().getName())
                        .pricePerDay(item.getPricePerDay())
                        .available(item.getAvailable())
                        .imageUrl(item.getImageUrl())
                        .build());
    }
}