package ru.Sidikov.tgBot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.Sidikov.tgBot.models.*;
import ru.Sidikov.tgBot.repositories.*;

import java.math.BigDecimal;

@SpringBootTest
public class FillingTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientOrderRepository clientOrderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Test
    void fillCategoriesAndProducts() {
        Category pizza = saveCategory("Пицца", null);
        Category rolls = saveCategory("Роллы", null);
        Category burgers = saveCategory("Бургеры", null);
        Category drinks = saveCategory("Напитки", null);

        // Подкатегории
        Category classicRolls = saveCategory("Классические роллы", rolls);
        Category bakedRolls = saveCategory("Запеченные роллы", rolls);
        Category sweetRolls = saveCategory("Сладкие роллы", rolls);
        Category sets = saveCategory("Наборы", rolls);

        Category classicBurgers = saveCategory("Классические бургеры", burgers);
        Category spicyBurgers = saveCategory("Острые бургеры", burgers);

        Category soda = saveCategory("Газированные напитки", drinks);
        Category energy = saveCategory("Энергетические напитки", drinks);
        Category juice = saveCategory("Соки", drinks);
        Category otherDrinks = saveCategory("Другие", drinks);

        // Пример товаров
        saveProduct("Пепперони", "Острая пицца с колбасой", new BigDecimal("480.00"), pizza);
        saveProduct("Маргарита", "Пицца с томатами и базиликом", new BigDecimal("420.00"), pizza);
        saveProduct("Сладкий ролл", "Рис, фрукты, сливочный сыр", new BigDecimal("350.00"), sweetRolls);
        saveProduct("Классический бургер", "Котлета, сыр, салат", new BigDecimal("390.00"), classicBurgers);
        saveProduct("Энергетик", "250 мл банка", new BigDecimal("180.00"), energy);
        saveProduct("Апельсиновый сок", "Свежевыжатый", new BigDecimal("200.00"), juice);
    }

    @Test
    void fillClientsAndOrders() {
        // Клиент
        Client client = new Client();
        client.setExternalId(1001L);
        client.setFullName("Иванов Иван");
        client.setPhoneNumber("89151234567");
        client.setAddress("г. Севастополь, ул. Морская, д. 7");
        client = clientRepository.save(client);

        // Заказ
        ClientOrder order = new ClientOrder();
        order.setClient(client);
        order.setStatus(1);
        order.setTotal(BigDecimal.ZERO);
        order = clientOrderRepository.save(order);

        // Выбор продукта
        Product product = productRepository.findAll().stream().findFirst().orElse(null);
        if (product == null) return;

        // Связь заказ-товар
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setClientOrder(order);
        orderProduct.setProduct(product);
        orderProduct.setCountProduct(2);
        orderProductRepository.save(orderProduct);

        // Обновление суммы
        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(2));
        order.setTotal(total);
        clientOrderRepository.save(order);
    }

    private Category saveCategory(String name, Category parent) {
        Category category = new Category();
        category.setName(name);
        category.setParent(parent);
        return categoryRepository.save(category);
    }

    private void saveProduct(String name, String description, BigDecimal price, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        productRepository.save(product);
    }
}
