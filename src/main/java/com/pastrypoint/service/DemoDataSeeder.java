package com.pastrypoint.service;

import com.pastrypoint.model.Product;
import com.pastrypoint.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DemoDataSeeder {

    private final ProductRepository productRepository;

    @PostConstruct
    public void seedProducts() {
        if (productRepository.count() > 0) {
            return;
        }
        seedBakeryProducts();
    }

    public void reseedProducts() {
        productRepository.deleteAll();
        seedBakeryProducts();
    }

    private void seedBakeryProducts() {
        Object[][] items = new Object[][] {
                {"Chocolate Truffle Cake", 520.0, "Cakes", "Rich chocolate sponge with ganache", "https://images.unsplash.com/photo-1578985545062-69928b1d9587?auto=format&fit=crop&w=600&q=80"},
                {"Black Forest Cake", 480.0, "Cakes", "Classic cherry and cream cake", "https://images.unsplash.com/photo-1606890737304-57a1ca8a5b62?auto=format&fit=crop&w=600&q=80"},
                {"Vanilla Celebration Cake", 450.0, "Cakes", "Soft vanilla sponge for birthdays", "https://images.unsplash.com/photo-1464349095431-e9a21285b5f3?auto=format&fit=crop&w=600&q=80"},
                {"Red Velvet Cake", 560.0, "Cakes", "Cream cheese frosted red velvet", "https://images.unsplash.com/photo-1586788680434-30d324b2d46f?auto=format&fit=crop&w=600&q=80"},
                {"Butterscotch Cake", 500.0, "Cakes", "Crunchy caramel butterscotch cake", "https://images.unsplash.com/photo-1557925923-cd4648e211a0?auto=format&fit=crop&w=600&q=80"},
                {"Pineapple Cake", 430.0, "Cakes", "Light pineapple cream cake", "https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?auto=format&fit=crop&w=600&q=80"},
                {"Mango Cream Cake", 520.0, "Cakes", "Seasonal mango layered cake", "https://images.unsplash.com/photo-1621303837174-89787a7d4729?auto=format&fit=crop&w=600&q=80"},
                {"Blueberry Cheesecake", 620.0, "Cakes", "Creamy baked cheesecake slice", "https://images.unsplash.com/photo-1533134242443-d4fd215305ad?auto=format&fit=crop&w=600&q=80"},

                {"Chocolate Pastry", 75.0, "Pastries", "Single serve chocolate pastry", "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=600&q=80"},
                {"Pineapple Pastry", 65.0, "Pastries", "Fresh cream pineapple pastry", "https://images.unsplash.com/photo-1488477181946-6428a0291777?auto=format&fit=crop&w=600&q=80"},
                {"Black Forest Pastry", 80.0, "Pastries", "Cherry chocolate pastry", "https://images.unsplash.com/photo-1624353365286-3f8d62daad51?auto=format&fit=crop&w=600&q=80"},
                {"Red Velvet Pastry", 90.0, "Pastries", "Soft red velvet slice", "https://images.unsplash.com/photo-1614707267537-b85aaf00c4b7?auto=format&fit=crop&w=600&q=80"},
                {"Mango Pastry", 85.0, "Pastries", "Mango cream pastry", "https://images.unsplash.com/photo-1605807646983-377bc5a76493?auto=format&fit=crop&w=600&q=80"},
                {"Coffee Pastry", 80.0, "Pastries", "Coffee cream pastry", "https://images.unsplash.com/photo-1551024506-0bccd828d307?auto=format&fit=crop&w=600&q=80"},
                {"Choco Lava Cup", 99.0, "Pastries", "Warm chocolate lava dessert", "https://images.unsplash.com/photo-1617305855058-336d24456869?auto=format&fit=crop&w=600&q=80"},

                {"Veg Puff", 30.0, "Puffs", "Flaky puff with veg filling", "https://images.unsplash.com/photo-1621241441637-ea2d3f59dbd3?auto=format&fit=crop&w=600&q=80"},
                {"Paneer Puff", 40.0, "Puffs", "Paneer masala puff", "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=600&q=80"},
                {"Egg Puff", 38.0, "Puffs", "Classic bakery egg puff", "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=600&q=80"},
                {"Chicken Puff", 55.0, "Puffs", "Spiced chicken puff", "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=600&q=80"},
                {"Corn Cheese Puff", 45.0, "Puffs", "Corn and cheese filling", "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=600&q=80"},
                {"Aloo Puff", 28.0, "Puffs", "Potato masala puff", "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=600&q=80"},
                {"Mushroom Puff", 50.0, "Puffs", "Mushroom pepper puff", "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=600&q=80"},

                {"Milk Bread", 45.0, "Breads", "Soft daily milk bread", "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=600&q=80"},
                {"Brown Bread", 55.0, "Breads", "Whole wheat brown bread", "https://images.unsplash.com/photo-1549931319-a545dcf3bc73?auto=format&fit=crop&w=600&q=80"},
                {"Garlic Bread", 75.0, "Breads", "Garlic butter bread loaf", "https://images.unsplash.com/photo-1573140247632-f8fd74997d5c?auto=format&fit=crop&w=600&q=80"},
                {"Burger Bun Pack", 60.0, "Breads", "Fresh burger buns", "https://images.unsplash.com/photo-1586190848861-99aa4a171e90?auto=format&fit=crop&w=600&q=80"},
                {"Pav Pack", 40.0, "Breads", "Soft pav pack", "https://images.unsplash.com/photo-1586444248902-2f64eddc13df?auto=format&fit=crop&w=600&q=80"},
                {"Croissant", 95.0, "Breads", "Buttery French croissant", "https://images.unsplash.com/photo-1555507036-ab1f4038808a?auto=format&fit=crop&w=600&q=80"},
                {"Focaccia Slice", 110.0, "Breads", "Herb focaccia slice", "https://images.unsplash.com/photo-1600628422019-5560c6d4a0b1?auto=format&fit=crop&w=600&q=80"},

                {"Choco Chip Cookies", 120.0, "Cookies", "Pack of crunchy cookies", "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?auto=format&fit=crop&w=600&q=80"},
                {"Butter Cookies", 110.0, "Cookies", "Classic butter cookies", "https://images.unsplash.com/photo-1590080874088-eec64895b423?auto=format&fit=crop&w=600&q=80"},
                {"Jeera Cookies", 90.0, "Cookies", "Savory cumin cookies", "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?auto=format&fit=crop&w=600&q=80"},
                {"Nankhatai", 100.0, "Cookies", "Indian bakery nankhatai", "https://images.unsplash.com/photo-1590080874088-eec64895b423?auto=format&fit=crop&w=600&q=80"},
                {"Oat Cookies", 130.0, "Cookies", "Oat and raisin cookies", "https://images.unsplash.com/photo-1590080874088-eec64895b423?auto=format&fit=crop&w=600&q=80"},
                {"Almond Biscotti", 145.0, "Cookies", "Crisp almond biscotti", "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?auto=format&fit=crop&w=600&q=80"},
                {"Brownie Bites", 150.0, "Cookies", "Mini chocolate brownie bites", "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=600&q=80"},

                {"Cold Coffee", 80.0, "Drinks", "Chilled cafe coffee", "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?auto=format&fit=crop&w=600&q=80"},
                {"Iced Tea", 70.0, "Drinks", "Lemon iced tea", "https://images.unsplash.com/photo-1497534446932-c925b458314e?auto=format&fit=crop&w=600&q=80"},
                {"Chocolate Shake", 110.0, "Drinks", "Thick chocolate shake", "https://images.unsplash.com/photo-1572490122747-3968b75cc699?auto=format&fit=crop&w=600&q=80"},
                {"Mango Shake", 110.0, "Drinks", "Fresh mango shake", "https://images.unsplash.com/photo-1623065422902-30a2d299bbe4?auto=format&fit=crop&w=600&q=80"},
                {"Masala Chai", 25.0, "Drinks", "Hot masala tea", "https://images.unsplash.com/photo-1561336526-2914f13ceb36?auto=format&fit=crop&w=600&q=80"},
                {"Filter Coffee", 35.0, "Drinks", "South Indian filter coffee", "https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=600&q=80"},
                {"Fresh Lime Soda", 55.0, "Drinks", "Sweet and salted lime soda", "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=600&q=80"},

                {"Mini Donut Box", 180.0, "Specials", "Assorted mini donuts", "https://images.unsplash.com/photo-1551024601-bec78aea704b?auto=format&fit=crop&w=600&q=80"},
                {"Cupcake Box", 220.0, "Specials", "Four cupcake combo", "https://images.unsplash.com/photo-1587668178277-295251f900ce?auto=format&fit=crop&w=600&q=80"},
                {"Brownie Sundae", 160.0, "Specials", "Brownie with ice cream", "https://images.unsplash.com/photo-1563805042-7684c019e1cb?auto=format&fit=crop&w=600&q=80"},
                {"Birthday Combo", 699.0, "Specials", "Cake, candles and knife", "https://images.unsplash.com/photo-1535141192574-5d4897c12636?auto=format&fit=crop&w=600&q=80"},
                {"Tea Time Combo", 149.0, "Specials", "Chai with puff and cookie", "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=600&q=80"},
                {"Family Snack Box", 299.0, "Specials", "Puffs and pastries combo", "https://images.unsplash.com/photo-1517433367423-c7e5b0f35086?auto=format&fit=crop&w=600&q=80"},
                {"Festival Sweet Box", 350.0, "Specials", "Bakery sweet gift box", "https://images.unsplash.com/photo-1571115177098-24ec42ed204d?auto=format&fit=crop&w=600&q=80"}
        };

        for (Object[] item : items) {
            Product product = new Product();
            product.setName((String) item[0]);
            product.setPrice((Double) item[1]);
            product.setCategory((String) item[2]);
            product.setDescription((String) item[3]);
            product.setImageUrl((String) item[4]);
            product.setStockQuantity(50);
            product.setInStock(true);
            productRepository.save(product);
        }
    }
}
