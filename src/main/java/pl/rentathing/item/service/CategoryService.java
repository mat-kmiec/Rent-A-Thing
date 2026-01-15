package pl.rentathing.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.rentathing.item.dto.CategoryDto;
import pl.rentathing.item.entity.Category;
import pl.rentathing.item.mapper.CategoryMapper;
import pl.rentathing.item.repository.CategoryRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toDtoList(categories);
    }

}
