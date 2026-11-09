package com.human.mini_prj.repository;

import com.human.mini_prj.Constant.ItemSellStatus;
import com.human.mini_prj.entity.Item;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository; //필드를 통한 의존성 주입

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest() {
        for(int i = 1; i <= 10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 * i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
            log.info("Item 저장 : {}", savedItem);
        }
    }
    @Test
    @DisplayName("상품 조회 테스트")
    public void findByItemNumtest() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품5");
        for(Item item : itemList) {
            log.info("상품 조회 테스트 : {}", item);
        }
    }

    @Test
    @DisplayName("상품명 or 상세 설명으로 조회")
    public void findByItemNmOrItemDetail() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품1","테스트 상품 상세 설명5");
        for(Item item : itemList) {
            log.info("상품명 or 상세 설명으로 조회 테스트 : {}", item);
        }
    }


    @Test
    @DisplayName("가격 LessThan 조회")
    public void findByPriceLessThan() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findByPriceLessThan(5000);
        for(Item item : itemList) {
            log.info("상품 LessThan 테스트 : {}", item);
        }
    }

    @Test
    @DisplayName("가격 특정 값 조회")
    public void findByPriceLessThanOrderByPriceDesc() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10000);
        for(Item item : itemList) {
            log.info("가격 특정 값 조회 테스트 : {}", item);
        }
    }

    @Test
    @DisplayName("가격 범위 검색")
    public void findByPriceBetween() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findByPriceBetween(10000,10005);
        for(Item item : itemList) {
            log.info("가격 범위 검색 테스트 : {}", item);
        }
    }

    @Test
    @DisplayName("특정 문자열 검색")
    public void findByItemNmContaining() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findByItemNmContaining("9");
        for(Item item : itemList) {
            log.info("특정 문자열 검색 테스트 : {}", item);
        }
    }

    @Test
    @DisplayName("판매 상태 검색")
    public void findByItemSellStatus() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findByItemSellStatus(ItemSellStatus.SOLD_OUT);
        for(Item item : itemList) {
            log.info("판매 상태 검색 테스트 : {}", item);
        }
    }

    @Test
    @DisplayName("판매 상태/특정 가격 검색")
    public void findByItemSellStatusAndPriceGreaterThanEqual() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findByItemSellStatusAndPriceGreaterThanEqual(ItemSellStatus.SELL,5000);
        for(Item item : itemList) {
            log.info("판매 상태/특정 가격 검색 테스트 : {}", item);
        }
    }

    @Test
    @DisplayName("가장 비싼 상품 3개 가격 검색")
    public void findTop3ByOrderByPriceDesc() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findTop3ByOrderByPriceDesc();
        for(Item item : itemList) {
            log.info("가장 비싼 상품 3개 가격 검색 테스트 : {}", item);
        }
    }

    @Test
    @DisplayName("가격 In 검색 테스트")
    public void findByPriceIn() {
        this.createItemTest();
        List<Integer> priceList = Arrays.asList(10000, 20000, 30000);
        List<Item> itemList = itemRepository.findByPriceIn(priceList);
        for(Item item : itemList) {
            log.info("가격 In 검색 테스트 : {}", item);
        }
    }

    @Test
    @DisplayName("판매 상태별 상품 개수 검색 테스트")
    public void countByItemSellStatus() {
        this.createItemTest();
        long totalCount = itemRepository.countByItemSellStatus(ItemSellStatus.SOLD_OUT);
        log.info("판매 상태별 상품 개수 검색 테스트 : {}", totalCount);
    }

    @Test
    @DisplayName("native query 조회")
    public void nativeQueryTest() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findByItemDetailLikeNative("설명5");
        for(Item item : itemList) {
            log.info("native query 조회 테스트 : {}", item);
        }
    }

}