package pl.rentathing.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.rentathing.item.dto.ItemDetailsDTO;
import pl.rentathing.item.entity.Item;
import pl.rentathing.item.exception.ItemNotFoundException;
import pl.rentathing.item.mapper.ItemMapper;
import pl.rentathing.item.repository.ItemRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id.toString()));
    }

    public void saveItem(Item item, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            item.setImageUrl("/uploads/items/" + imageFile.getOriginalFilename());
        }

        if (item.getId() == null) {
            if (item.getAverageRating() == null) item.setAverageRating(0.0);
        }

        itemRepository.save(item);
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
}