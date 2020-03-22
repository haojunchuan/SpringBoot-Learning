package com.leelovejava.es;

import com.alibaba.fastjson.JSON;
import com.leelovejava.essearch.ElasticSearchApplication;
import com.leelovejava.essearch.document.ProductDocument;
import com.leelovejava.essearch.document.ProductDocumentBuilder;
import com.leelovejava.essearch.page.Page;
import com.leelovejava.essearch.service.EsSearchService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticSearchApplication.class)
public class ElasticSearchApplicationTests {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private EsSearchService esSearchService;

    @Test
    public void save() {
        log.info("【创建索引前的数据条数】：{}", esSearchService.getAll().size());

        ProductDocument productDocument = ProductDocumentBuilder.create()
                .addId(System.currentTimeMillis() + "01")
                .addProductName("无印良品 MUJI 基础润肤化妆水")
                .addProductDesc("无印良品 MUJI 基础润肤化妆水 高保湿型 200ml")
                .addCreateTime(new Date()).addUpdateTime(new Date())
                .builder();

        ProductDocument productDocument1 = ProductDocumentBuilder.create()
                .addId(System.currentTimeMillis() + "02")
                .addProductName("荣耀 V10 尊享版")
                .addProductDesc("荣耀 V10 尊享版 6GB+128GB 幻夜黑 移动联通电信4G全面屏游戏手机 双卡双待")
                .addCreateTime(new Date()).addUpdateTime(new Date())
                .builder();

        ProductDocument productDocument2 = ProductDocumentBuilder.create()
                .addId(System.currentTimeMillis() + "03")
                .addProductName("资生堂(SHISEIDO) 尿素红罐护手霜")
                .addProductDesc("日本进口 资生堂(SHISEIDO) 尿素红罐护手霜 100g/罐 男女通用 深层滋养 改善粗糙")
                .addCreateTime(new Date()).addUpdateTime(new Date())
                .builder();

        esSearchService.save(productDocument, productDocument1, productDocument2);

        log.info("【创建索引ID】:{},{},{}", productDocument.getId(), productDocument1.getId(), productDocument2.getId());
        log.info("【创建索引后的数据条数】：{}", esSearchService.getAll().size());
    }

    @Test
    public void getAll() {
        esSearchService.getAll().parallelStream()
                .map(JSON::toJSONString)
                .forEach(System.out::println);
    }

    /**
     * 删除全部
     */
    @Test
    public void deleteAll() {
        esSearchService.deleteAll();
        log.info("【根据ID查询内容】");
    }

    /**
     * 根据ID查询内容
     */
    @Test
    public void getById() {
        log.info("【根据ID查询内容】：{}", JSON.toJSONString(esSearchService.getById("154518851758102")));
    }

    @Test
    public void query() {
        log.info("【根据关键字搜索内容】：{}", JSON.toJSONString(esSearchService.query("无印良品荣耀", ProductDocument.class)));
    }

    @Test
    public void queryHit() {

        String keyword = "联通尿素";
        String indexName = "orders";

        List<Map<String, Object>> searchHits = esSearchService.queryHit(keyword, indexName, "productName", "productDesc");
        log.info("【根据关键字搜索内容，命中部分高亮，返回内容】：{}", JSON.toJSONString(searchHits));
        //[{"highlight":{"productDesc":"<span style='color:red'>无印良品</span> MUJI 基础润肤化妆水 高保湿型 200ml","productName":"<span style='color:red'>无印良品</span> MUJI 基础润肤化妆水"},"source":{"productDesc":"无印良品 MUJI 基础润肤化妆水 高保湿型 200ml","createTime":1544755966204,"updateTime":1544755966204,"id":"154475596620401","productName":"无印良品 MUJI 基础润肤化妆水"}},{"highlight":{"productDesc":"<span style='color:red'>荣耀</span> V10 尊享版 6GB+128GB 幻夜黑 移动联通电信4G全面屏游戏手机 双卡双待","productName":"<span style='color:red'>荣耀</span> V10 尊享版"},"source":{"productDesc":"荣耀 V10 尊享版 6GB+128GB 幻夜黑 移动联通电信4G全面屏游戏手机 双卡双待","createTime":1544755966204,"updateTime":1544755966204,"id":"154475596620402","productName":"荣耀 V10 尊享版"}}]
    }

    @Test
    public void queryHitByPage() {

        String keyword = "联通尿素";
        String indexName = "orders";

        Page<Map<String, Object>> searchHits = esSearchService.queryHitByPage(1, 1, keyword, indexName, "productName", "productDesc");
        log.info("【分页查询，根据关键字搜索内容，命中部分高亮，返回内容】：{}", JSON.toJSONString(searchHits));
        //[{"highlight":{"productDesc":"<span style='color:red'>无印良品</span> MUJI 基础润肤化妆水 高保湿型 200ml","productName":"<span style='color:red'>无印良品</span> MUJI 基础润肤化妆水"},"source":{"productDesc":"无印良品 MUJI 基础润肤化妆水 高保湿型 200ml","createTime":1544755966204,"updateTime":1544755966204,"id":"154475596620401","productName":"无印良品 MUJI 基础润肤化妆水"}},{"highlight":{"productDesc":"<span style='color:red'>荣耀</span> V10 尊享版 6GB+128GB 幻夜黑 移动联通电信4G全面屏游戏手机 双卡双待","productName":"<span style='color:red'>荣耀</span> V10 尊享版"},"source":{"productDesc":"荣耀 V10 尊享版 6GB+128GB 幻夜黑 移动联通电信4G全面屏游戏手机 双卡双待","createTime":1544755966204,"updateTime":1544755966204,"id":"154475596620402","productName":"荣耀 V10 尊享版"}}]
    }

    @Test
    public void deleteIndex() {
        log.info("【删除索引库】");
        esSearchService.deleteIndex("orders");
    }

    /**
     * 聚合查询
     */
    @Test
    public void testAgg() {
        log.info("【聚合查询】");
        // 创建一个查询条件对象
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 拼接查询条件
        queryBuilder.should(QueryBuilders.termQuery("字段", "值"));

        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        // 2、查询,需要把结果强转为AggregatedPage类型
        // AggregatedPage在Page功能的基础上，拓展了与聚合相关的功能
        // boolean hasAggregations(); 判断结果中是否有聚合
        // Aggregations getAggregations(); 获取所有聚合形成的map,key是聚合名称
        // Aggregation getAggregation(String var1); 根据聚合名称,获取指定集合
        AggregatedPage<ProductDocument> aggPage = esSearchService.queryAggregation(queryBuilder, ProductDocument.class);
        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        // brands: 定义的聚合名称
        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3、遍历
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称
            System.out.println(bucket.getKeyAsString());
            // 3.5、获取桶中的文档数量
            System.out.println(bucket.getDocCount());
        }

    }

}

