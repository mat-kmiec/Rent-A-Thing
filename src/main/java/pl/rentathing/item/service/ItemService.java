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

/**
 * Service class responsible for managing Item entities.
 * Provides methods for retrieving, saving, and manipulating Item data.
 */
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final CategoryRepository categoryRepository;

    /**
     * Retrieves an item by its unique identifier.
     *
     * @param id the unique identifier of the item to retrieve
     * @return the Item with the specified identifier
     * @throws ItemNotFoundException if no item with the given identifier is found
     */
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id.toString()));
    }

    /**
     * The directory where uploaded files are stored.
     * Configured through the `app.upload.dir` property in the application settings.
     */
    @Value("${app.upload.dir}")
    private String uploadDir;

    /**
     * Saves or updates an item based on the provided item DTO. This method checks whether the item already exists
     * by its ID. If the ID is null, a new item is created. If an image file is provided in the DTO, it attempts to save
     * the image and update the item's image URL. The item is then saved to the persistent storage along with its
     * associated category.
     *
     * @param itemDto the data transfer object containing details of the item to be saved or updated
     *                including its ID, category ID, image file, and other attributes
     * @throws IllegalArgumentException if the provided ID or category ID is invalid and cannot be found
     * @throws RuntimeException if saving the image fails
     */
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

    /**
     * Retrieves an item form as a data transfer object (DTO) based on its unique identifier.
     * Converts the retrieved item entity into a form DTO for further processing or representation.
     *
     * @param id the unique identifier of the item to retrieve
     * @return an ItemFormDTO containing the details of the item associated with the provided ID
     * @throws IllegalArgumentException if no item with the given ID is found
     */
    public ItemFormDTO getItemFormById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ID: " + id));
        return itemMapper.toDto(item);
    }

    /**
     * Saves an image file to the configured upload directory and returns the relative path to the saved file.
     * If the upload directory does not exist, it will be created.
     *
     * @param file the multipart file to be saved
     * @return the relative path to the saved image file
     * @throws IOException if an I/O error occurs during file saving
     */
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

    /**
     * Retrieves the details of an item based on the provided item ID.
     *
     * @param id the unique identifier of the item to retrieve
     * @return an ItemDetailsDTO object containing the detailed information of the specified item,
     *         including the discounted price
     * @throws ItemNotFoundException if no item is found with the given ID
     */
    public ItemDetailsDTO getItemDetails(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id.toString()));
        ItemDetailsDTO dto = itemMapper.toDetailsDTO(item);
        dto.setDiscountedPrice(calculateDiscountedPrice(item));
        return dto;
    }

    /**
     * Calculates the discounted price for a given item based on its price per day and discount percentage.
     * If the discount percentage is null or less than or equal to zero, the original price is returned.
     *
     * @param item the item containing the price per day and discount percentage
     * @return the discounted price of the item as a BigDecimal
     */
    private BigDecimal calculateDiscountedPrice(Item item) {
        if (item.getDiscountedPercent() == null || item.getDiscountedPercent() <= 0) {
            return item.getPricePerDay();
        }
        return item.getPricePerDay().subtract(
                item.getPricePerDay().multiply(BigDecimal.valueOf(item.getDiscountedPercent()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
        );
    }

    /**
     * Retrieves a paginated list of admin items based on the provided search criteria.
     *
     * @param criteria the search criteria used to filter the items
     * @param pageable the pagination information, including page number and size
     * @return a page containing a list of items mapped to ItemAdminListDTO objects
     */
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