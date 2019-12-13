package com.waps.elastic.search.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.waps.elastic.search.utils.PageUtils;
import com.waps.utils.StringUtils;
import com.waps.utils.TemplateUtils;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.*;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.SearchModule;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

@Service("esClient")
public class ESClient {

    @Resource
    private RestHighLevelClient client;

    protected String INDEX_NAME = "";
    protected String TYPE_NAME = "";
    protected String INDEX_MAPPING = "";

    public final static String DEF_ORDER_RANDOM = "random";
    public final static String DEF_ORDER_EXPLAIN = "explain";

    public ESClient() {

    }

    /**
     * 单独检测索引是否存在
     *
     * @return
     */
    public boolean isIndexExists() {
        GetIndexRequest request = new GetIndexRequest();
        try {
            request.indices(INDEX_NAME);
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            System.out.println("isIndexExists ERROR:" + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * 自动检测索引是否存在，异步
     */
    public void autoCheck() {
        new Thread() {
            public void run() {
                for (int i = 0; i < 20; i++) {
                    if (client != null) {
                        checkIndex();
                        break;
                    }
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 检测索引是否存在，并创建mapping
     */
    public void check() {
        autoCheck();
    }

    public void checkIndex() {
        boolean flg = isIndexExists();
        System.out.println("检查索引:" + INDEX_NAME + "/" + TYPE_NAME + " 存在:" + flg);
        if (!flg) {
            checkIndex(INDEX_NAME, TYPE_NAME, INDEX_MAPPING);
        }
    }

    /**
     * 插入文档，ID由ES随机生成
     *
     * @param object
     * @return
     */
    public IndexResponse save(Object object) {
        return saveEntity(INDEX_NAME, TYPE_NAME, object);
    }

    /**
     * 插入文档，指定ID，可维持文档唯一性
     *
     * @param id
     * @param object
     * @return
     */
    public IndexResponse save(String id, Object object) {
        return saveEntity(INDEX_NAME, TYPE_NAME, id, object);
    }

    public BulkResponse saveBulk(List<Object> list) {
        return saveBulkEntity(INDEX_NAME, TYPE_NAME, list);
    }

    /**
     * 一次更新一个字段属性
     *
     * @param id
     * @param field
     * @param value
     * @return
     */
    public UpdateResponse update(String id, String field, Object value) {

        return updateField(INDEX_NAME, TYPE_NAME, id, field, value);
    }

    /**
     * 一次更新多个字段属性
     *
     * @param id
     * @param updateParams
     * @return
     */
    public UpdateResponse update(String id, Map<String, Object> updateParams) {

        return updateParams(INDEX_NAME, TYPE_NAME, id, updateParams);

    }

    /**
     * 一次更新多个字段属性，私有
     *
     * @param index
     * @param type
     * @param id
     * @param updateParams
     * @return
     */
    private UpdateResponse updateParams(String index, String type, String id, Map<String, Object> updateParams) {
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        Iterator it = updateParams.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            Object value = updateParams.get(key);
            if (value == null) {
                it.remove();
            }
        }
        updateRequest.doc(updateParams);
        try {
            return client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            System.out.println("updateParams " + index + " " + type + " " + id + " ERROR:" + e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * 一次更新一个字段属性，私有
     *
     * @param index
     * @param type
     * @param id
     * @param field
     * @param value
     * @return
     */
    private UpdateResponse updateField(String index, String type, String id, String field, Object value) {
        UpdateRequest request = new UpdateRequest(
                index,//索引
                type,//类型
                id);//文档ID
        if (value instanceof Integer) {
            int new_value = (Integer) value;
            request.script(new Script("ctx._source." + field + " = " + new_value));
        } else if (value instanceof Double) {
            Double new_value = (Double) value;
            request.script(new Script("ctx._source." + field + " = " + new_value));
        } else if (value instanceof Float) {
            Float new_value = (Float) value;
            request.script(new Script("ctx._source." + field + " = " + new_value));
        } else if (value instanceof Long) {
            Long new_value = (Long) value;
            request.script(new Script("ctx._source." + field + " = " + new_value));
        } else {
            request.script(new Script("ctx._source." + field + " = \"" + value + "\""));
        }
        try {
            return client.update(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            System.out.println("updateField " + index + " " + type + " " + id + " ERROR:" + e.getLocalizedMessage());
            return null;
        }

    }


    /**
     * 用key+value方式查找结果，精确，可翻页，可多个key+value
     *
     * @param kvMap
     * @param page
     * @param size
     * @return
     */
    public SearchHits findByKVMap(Map<String, String> kvMap, int page, int size) {
        PageUtils pageUtils = new PageUtils(page, size);
        SearchHits hits = null;
        try {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            Iterator it = kvMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entity = (Map.Entry) it.next();
                String field = (String) entity.getKey();
                String value = (String) entity.getValue();
                boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(field, value));
            }
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(boolQueryBuilder);
            sourceBuilder.explain(true);// 设置是否按查询匹配度排序
            sourceBuilder.from(pageUtils.getFrom());
            sourceBuilder.size(pageUtils.getSize());
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.source(sourceBuilder);
            hits = find(searchRequest);
        } catch (Exception e) {
            System.out.println("findByKVMap ERROR:" + e.getLocalizedMessage());
        }
        return hits;
    }


    /**
     * 搜索底层用法，更灵活
     *
     * @param searchRequest
     * @return
     */
    public SearchHits find(SearchRequest searchRequest) {
        try {
            searchRequest.indices(INDEX_NAME);
            searchRequest.types(TYPE_NAME);
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            return response.getHits();
        } catch (Exception e) {
            System.out.println("find ERROR:" + e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * 列出全部内容, 支持分页，无排序字段
     *
     * @param page
     * @param size
     * @return
     */
    public SearchHits findAll(int page, int size) {
        return findAll(null, null, page, size);
    }

    /**
     * 列出全部内容, 支持分页，排序字段，默认倒序
     *
     * @param page
     * @param size
     * @param orderField
     * @return
     */
    public SearchHits findAll(int page, int size, String orderField) {
        return findAll(orderField, SortOrder.DESC, page, size);
    }

    /**
     * 列出全部内容，支持分页和排序
     *
     * @param page
     * @param size
     * @param orderField
     * @param sortOrder
     * @return
     */
    public SearchHits findAll(String orderField, SortOrder sortOrder, int page, int size) {
        try {
            return searchAll(INDEX_NAME, TYPE_NAME, orderField, sortOrder, page, size);
        } catch (Exception e) {
            System.out.println("findAll " + INDEX_NAME + "  " + TYPE_NAME + "  ERROR:" + e.getLocalizedMessage());
            return null;
        }
    }


    public SearchHits findMatch(String matchTag, String matchValue, String orderField, SortOrder sortOrder, int page, int size) {
        return searchMatch(INDEX_NAME, TYPE_NAME, matchTag, matchValue, orderField, sortOrder, page, size);
    }

    public SearchHits findTerm(String termTag, String termValue, String orderField, SortOrder sortOrder, int page, int size) {
        return searchTerm(INDEX_NAME, TYPE_NAME, termTag, termValue, orderField, sortOrder, page, size);
    }

    /**
     * 通过ES json脚本查询，可高度自定义，脚本控制排序
     *
     * @param script
     * @return
     */
    public SearchHits findByScript(String script) {
        try {
            SearchResponse response = searchByJson(INDEX_NAME, TYPE_NAME, script);
            return response.getHits();
        } catch (Exception e) {
            System.out.println("findByScript " + INDEX_NAME + "  " + TYPE_NAME + "  ERROR:" + e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * 执行Group类型的脚本，script为脚本内容
     *
     * @param script
     * @return
     */
    public Aggregations groupByScript(String script) {
        try {
            SearchResponse response = searchByJson(INDEX_NAME, TYPE_NAME, script);
            return response.getAggregations();
        } catch (Exception e) {
            System.out.println("findByScript " + INDEX_NAME + "  " + TYPE_NAME + "  ERROR:" + e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * 执行Group类型的脚本，scriptPath为脚本路径
     *
     * @param scriptPath
     * @return
     */
    public Aggregations groupByScriptFromResource(String scriptPath) {
        File mappingFile;
        try {
            mappingFile = new File(this.getClass().getClassLoader().getResource(scriptPath).getFile());
        } catch (Exception e) {
            System.out.println("groupByScriptFromResource ERROR:" + e.getLocalizedMessage());
            return null;
        }
        if (mappingFile.exists()) {
            try {
                String json = FileUtils.readFileToString(mappingFile);
                return groupByScript(json);
            } catch (Exception e) {
                System.out.println("groupByScriptFromResource ERROR:" + e.getLocalizedMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 从资源目录读取freemarker模版脚本，脚本控制排序
     *
     * @param freeMarkerFtlPath
     * @param template_params
     * @return
     */
    public Aggregations groupByFreeMarkerFromResource(String freeMarkerFtlPath, HashMap template_params) {
        String json = null;
        try {
            json = new TemplateUtils().getFreeMarkerFromResource(freeMarkerFtlPath, template_params, "UTF-8");
        } catch (Exception e) {
            System.out.println("groupByFreeMarkerFromResource error:" + e.getLocalizedMessage());
        }
        if (!StringUtils.isNull(json)) {
            try {
                return groupByScript(json);
            } catch (Exception e) {
                System.out.println("groupByFreeMarkerFromResource " + INDEX_NAME + "  " + TYPE_NAME + "  ERROR:" + e.getLocalizedMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 从资源目录下读取脚本文件，脚本控制排序
     *
     * @param scriptPath
     * @return
     */
    public SearchHits findByScriptFromResource(String scriptPath) {
        File mappingFile;
        try {
            mappingFile = new File(this.getClass().getClassLoader().getResource(scriptPath).getFile());
        } catch (Exception e) {
            System.out.println("findByScriptFromResource ERROR:" + e.getLocalizedMessage());
            return null;
        }
        if (mappingFile.exists()) {
            try {
                String json = FileUtils.readFileToString(mappingFile);
                return findByScript(json);
            } catch (Exception e) {
                System.out.println("findByScriptFromResource ERROR:" + e.getLocalizedMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 从资源目录读取freemarker模版脚本，脚本控制排序
     *
     * @param freeMarkerFtlPath
     * @param template_params
     * @return
     */
    public SearchHits findByFreeMarkerFromResource(String freeMarkerFtlPath, HashMap template_params) {
        String json = null;
        try {
            json = new TemplateUtils().getFreeMarkerFromResource(freeMarkerFtlPath, template_params, "UTF-8");
            System.out.println(json);
        } catch (Exception e) {
            System.out.println("freemarkerTemplate error:" + e.getLocalizedMessage());
        }
        if (!StringUtils.isNull(json)) {
            try {
                return findByScript(json);
            } catch (Exception e) {
                System.out.println("findByScript " + INDEX_NAME + "  " + TYPE_NAME + "  ERROR:" + e.getLocalizedMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 检测Index是否存在,ESClient内部使用
     *
     * @param index
     * @return
     */
    private boolean checkIndex(String index, String type, String mappingPath) {
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.settings(Settings.builder()
                .put("index.number_of_shards", 5)
                .put("index.number_of_replicas", 0)
                .put("index.max_result_window", 999999)
        );
//        File mappingFile;
        StringBuffer buffer = new StringBuffer();
        try {
//            mappingFile = new File(this.getClass().getClassLoader().getResource(mappingPath).getFile());

            InputStream is = this.getClass().getClassLoader().getResourceAsStream(mappingPath);
            //InputStream is=当前类.class.getResourceAsStream("XX.config");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String s = "";

            while ((s = br.readLine()) != null)
                buffer.append(s);
        } catch (Exception e) {
            System.out.println("查找ES mapping配置文件出错, " + e.getLocalizedMessage());
            System.out.println("无法创建索引:" + index + " " + type);
            return false;
        }

        try {
//            String mappingSrc = FileUtils.readFileToString(mappingFile);
            request.mapping(type, buffer.toString(), XContentType.JSON);
            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
            return createIndexResponse.isAcknowledged();
        } catch (Exception e) {
            System.out.println("检查索引信息:" + index + "/" + type + "  " + mappingPath + "   error:" + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * json转指定对象,这里采用阿里的类处理，可以解决内部类型转换问题
     *
     * @param json
     * @param objClass
     * @return
     */
    public <T> Object getObjectFromJson(String json, Class<T> objClass) {
        return JSON.parseObject(json, objClass);
    }

    /**
     * sf.json处理，内部数组也是json的无法转化
     *
     * @param json
     * @param objClass
     * @param <T>
     * @return
     */
    public <T> Object getObjectFromSFJson(String json, Class<T> objClass) {
        net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(json);
        Object obj = net.sf.json.JSONObject.toBean(jsonObject, objClass);
        return obj;
    }

    /**
     * 读取指定文档
     *
     * @param id
     * @return
     */
    public GetResponse load(String id) {
        return getObjectEntity(INDEX_NAME, TYPE_NAME, id);
    }

    /**
     * 读取指定文档，并转成指定类
     *
     * @param id
     * @param zClass
     * @param <T>
     * @return
     */
    public <T> Object load(String id, Class<T> zClass) {
        GetResponse response = getObjectEntity(INDEX_NAME, TYPE_NAME, id);
        return getObjectFromJson(response.getSourceAsString(), zClass);
    }

    /**
     * 读取指定文档，私有
     *
     * @param index
     * @param type
     * @param id
     * @return
     */
    private GetResponse getObjectEntity(String index, String type, String id) {
        GetRequest request = new GetRequest(
                index,   //索引
                type,     // mapping type
                id);     //文档id
        GetResponse response = null;
        try {
            response = client.get(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            System.out.println("getObjectEntity " + index + " " + id + "  ERROR:" + e.getLocalizedMessage());
        }
        return response;
    }

    /**
     * 删除指定文档
     *
     * @param id
     * @return
     */
    public DeleteResponse delete(String id) {
        return deleteEntity(INDEX_NAME, TYPE_NAME, id);
    }

    /**
     * 删除指定文档，私有
     *
     * @param index
     * @param type
     * @param id
     * @return
     */
    private DeleteResponse deleteEntity(String index, String type, String id) {
        DeleteRequest deleteRequest = new DeleteRequest(index, type, id);
        DeleteResponse response = null;
        try {
            response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            System.out.println("deleteEntity " + index + " " + id + "  ERROR:" + e.getLocalizedMessage());
        }
        return response;
    }

    /**
     * 通过翻页方式查找所有记录，不带条件，私有
     *
     * @param index
     * @param type
     * @param page
     * @param size
     * @return
     */
    private SearchHits searchAll(String index, String type, String orderField, SortOrder sortOrder, int page, int size) throws Exception {
        PageUtils pageUtils = new PageUtils(page, size);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(pageUtils.getFrom());
        sourceBuilder.size(pageUtils.getSize());
        // 排序
        if (!StringUtils.isNull(orderField)) {
            FieldSortBuilder fsb = SortBuilders.fieldSort(orderField);
            if (sortOrder != null) {
                fsb.order(sortOrder);
            }
            sourceBuilder.sort(fsb);
        }

        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        return response.getHits();
    }

    private SearchHits searchByMoreField(String index, String type, LinkedHashMap<String, Object> kvMap, String orderField, SortOrder orderBy, int page, int size) {
        PageUtils pageUtils = new PageUtils(page, size);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        Iterator it = kvMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entity = (Map.Entry) it.next();
            String field = (String) entity.getKey();
            String value = (String) entity.getValue();
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(field, value));
        }

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.explain(true);// 设置是否按查询匹配度排序
        sourceBuilder.from(pageUtils.getFrom());
        sourceBuilder.size(pageUtils.getSize());
        if (!StringUtils.isNull(orderField)) {
            sourceBuilder.sort(orderField, orderBy);
        }

        //查询建立
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            return response.getHits();
        } catch (IOException e) {
            System.out.println("searchByMoreField ERROR:" + e.getLocalizedMessage());
            return null;
        }

    }

    /**
     * 分析器字段匹配查询
     * termQuery:不带分析器,matchQuery:带分析器
     *
     * @param index
     * @param type
     * @param matchTag
     * @param matchValue
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    private SearchHits searchMatch(String index, String type, String matchTag, String matchValue, String orderField, SortOrder orderBy, int page, int size) {
        PageUtils pageUtils = new PageUtils(page, size);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(pageUtils.getFrom());
        sourceBuilder.size(pageUtils.getSize());
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(matchTag, matchValue);
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(matchQueryBuilder);
        sourceBuilder.query(boolBuilder);
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.source(sourceBuilder);
        //
        //判断是否随机排序
        if (orderField.equalsIgnoreCase(DEF_ORDER_RANDOM)) {
            String script = "Math.random()";
            SortBuilder sortByRandom = SortBuilders.scriptSort(new Script(script), ScriptSortBuilder.ScriptSortType.NUMBER).order(orderBy);
            sourceBuilder.sort(sortByRandom);
        } else {
            sourceBuilder.sort(orderField, orderBy);
        }
        if (orderField.equalsIgnoreCase(DEF_ORDER_EXPLAIN)) {
            sourceBuilder.explain(true);
        }
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            return response.getHits();
        } catch (IOException e) {
            System.out.println("searchMatch ERROR:" + e.getLocalizedMessage());
            return null;
        }
    }

    private SearchHits searchTerm(String index, String type, String termTag, String termValue, String orderField, SortOrder orderBy, int page, int size) {
        PageUtils pageUtils = new PageUtils(page, size);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(pageUtils.getFrom());
        sourceBuilder.size(pageUtils.getSize());
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(termTag, termValue);
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(termQueryBuilder);
        sourceBuilder.query(boolBuilder);
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.source(sourceBuilder);
        //
        //判断是否随机排序
        if (orderField.equalsIgnoreCase(DEF_ORDER_RANDOM)) {
            String script = "Math.random()";
            SortBuilder sortByRandom = SortBuilders.scriptSort(new Script(script), ScriptSortBuilder.ScriptSortType.NUMBER).order(orderBy);
            sourceBuilder.sort(sortByRandom);
        } else {
            sourceBuilder.sort(orderField, orderBy);
        }
        if (orderField.equalsIgnoreCase(DEF_ORDER_EXPLAIN)) {
            sourceBuilder.explain(true);
        }
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            return response.getHits();
        } catch (IOException e) {
            System.out.println("searchMatch ERROR:" + e.getLocalizedMessage());
            return null;
        }
    }


    /**
     * 通过脚本搜索，支持翻页，私有
     *
     * @param index_name
     * @param type_name
     * @param jsonScript
     * @return
     * @throws Exception
     */
    private SearchResponse searchByJson(String index_name, String type_name, String jsonScript) throws Exception {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        Settings settings = Settings.builder().build();
        SearchModule searchModule = new SearchModule(settings, false, new ArrayList<>());
        NamedXContentRegistry xContentRegistry = new NamedXContentRegistry(searchModule.getNamedXContents());
        XContentParser parser = XContentFactory.xContent(XContentType.JSON).createParser(xContentRegistry, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, jsonScript);
        searchSourceBuilder.parseXContent(parser);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index_name);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        return response;
    }

    /**
     * 底层调用搜索，私有
     *
     * @param request
     * @return
     */
    private SearchResponse searchResponse(SearchRequest request) {
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            if (response.getHits() == null) {
                return null;
            }
            return response;
        } catch (Exception e) {
            System.out.println("ESClient searchResponse ERROR:" + e.getLocalizedMessage());
        }
        return null;
    }

    private List<JSONObject> search(SearchRequest request) {
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            if (response.getHits() == null) {
                return null;
            }
            List<JSONObject> list = new ArrayList<>();
            response.getHits().forEach(item -> list.add(JSON.parseObject(item.getSourceAsString())));
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> searchString(SearchRequest request) {
        try {

            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            if (response.getHits() == null) {
                return null;
            }
            List<String> list = new ArrayList<>();
            response.getHits().forEach(item -> list.add(item.getSourceAsString()));
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Long searchCount(SearchRequest request) {
        try {

            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            if (response.getHits() == null) {
                return 0L;
            }

            long total = response.getHits().getTotalHits().value;
            return total;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    private <T> IndexResponse saveEntity(String index, String type, T t) {
        IndexResponse indexResponse = null;
        try {
            IndexRequest indexRequest = new IndexRequest(index, type);
            indexRequest.source(JSON.toJSONString(t), XContentType.JSON);
            /*BulkRequest bulkRequest = new BulkRequest();
            bulkRequest.add(indexRequest);
            Header basicHeader = new BasicHeader("Content-Type:application" , "json");*/
            //this.client.bulk(bulkRequest , basicHeader);
            indexResponse = this.client.index(indexRequest, RequestOptions.DEFAULT);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return indexResponse;
    }

    private <T> IndexResponse saveEntity(String index, String type, String id, T t) {
        IndexResponse indexResponse = null;
        try {
            IndexRequest indexRequest = new IndexRequest(index, type, id);
            indexRequest.source(JSON.toJSONString(t), XContentType.JSON);
            /*BulkRequest bulkRequest = new BulkRequest();
            bulkRequest.add(indexRequest);
            Header basicHeader = new BasicHeader("Content-Type:application" , "json");*/
            //this.client.bulk(bulkRequest , basicHeader);
            indexResponse = this.client.index(indexRequest, RequestOptions.DEFAULT);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return indexResponse;
    }

    private BulkResponse saveBulkEntity(String index, String type, List<Object> list) {
        BulkRequest bulkRequest = new BulkRequest();

        for (int i = 0; i < list.size(); i++) {
            Object object = list.get(i);

            JSONObject object1 = (JSONObject) JSON.toJSON(object);
            if (object1.get("id") != null) {
                String id = (String) object1.get("id");
                String json = JSON.toJSONString(object);
                IndexRequest indexRequest = new IndexRequest(index, type, id);
                indexRequest.source(json, XContentType.JSON);
                bulkRequest.add(indexRequest);
            }
        }
        try {
            return this.client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            System.out.println("BulkSaveEntity ERROR:" + e.getLocalizedMessage());
            return null;
        }
    }

    private <T> List<T> searchScroll(SearchRequest searchRequest, Class<T> tClass) {

        try {

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            String scrollId = searchResponse.getScrollId();
            SearchHit[] hits = searchResponse.getHits().getHits();
            List<T> list = new ArrayList<>();
            for (SearchHit searchHit : hits) {
                list.add(JSON.parseObject(searchHit.getSourceAsString(), tClass));
            }

            Scroll scroll = new Scroll(TimeValue.timeValueMinutes(5L));
            while (hits != null && hits.length > 0) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                searchResponse = client.searchScroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = searchResponse.getScrollId();
                hits = searchResponse.getHits().getHits();
                for (SearchHit searchHit : hits) {
                    list.add(JSON.parseObject(searchHit.getSourceAsString(), tClass));
                }
            }
            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            boolean succeeded = clearScrollResponse.isSucceeded();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
