package com.waps.union_jd_api.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.waps.service.jd.api.bean.CategoryParams;
import com.waps.service.jd.api.service.JdUnionService;
import com.waps.service.jd.es.domain.JDCategoryESMap;
import com.waps.service.jd.es.service.JDCategoryESService;
import com.robot.netty.server.OnLineService;
import com.waps.union_jd_api.utils.JDConfig;
import com.waps.utils.StringUtils;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Component
public class JDCategoryService {

    @Autowired
    private JDCategoryESService jdCategoryESService;

    @Autowired
    private JdUnionService jdUnionService;

    JDCategoryService() {
//        loadAllCategory2Map(1);
    }

    public List<JDCategoryESMap> loadCategoryList(String type) {
        LinkedHashMap<String,String> linkedHashMap=new LinkedHashMap<>();
        String config_json = "api/category_type.json";
        File typeJson = new File(this.getClass().getClassLoader().getResource(config_json).getFile());
        String json = StringUtils.getFileTxt(typeJson.getPath());
        JSONObject jsonObject = (JSONObject) JSONObject.parse(json);

        if (!StringUtils.isNull(type)) {
            JSONArray typeArray = (JSONArray) jsonObject.get(type);
            for (Object value : typeArray) {
                String id = (String) value;
                linkedHashMap.put(id,id);
            }
        } else {
            Iterator it = jsonObject.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                JSONArray typeArray = (JSONArray) jsonObject.get(key);
                if(typeArray!=null) {
                    for (Object value : typeArray) {
                        if(value!=null) {
                            String id = (String) value;
                            if(!StringUtils.isNull(id)) {
                                linkedHashMap.put(id,id);
                            }
                        }
                    }
                }
            }
        }

        List<String> cid_list = new ArrayList<>(linkedHashMap.keySet());
        return getCategoryList(cid_list);
    }

    public List<JDCategoryESMap> getCategoryList(List<String> cid_list) {
        if (OnLineService.getJdCategoryMap().size() < 1) {
            loadAllCategory2Map(1);
        }
        List<JDCategoryESMap> cList = new ArrayList<>();
        if (cid_list != null && cid_list.size() > 0) {
            for (String cid : cid_list) {
                JDCategoryESMap jdCategoryESMap = OnLineService.getJdCategoryMap().get(cid);
                cList.add(jdCategoryESMap);
            }
        }
        return cList;
    }

    public void startSyncAllCategory() {
        CategoryParams categoryParams = new CategoryParams();
        categoryParams.setApp_key(JDConfig.APP_KEY);
        categoryParams.setApp_secret(JDConfig.SECRET_KEY);
        categoryParams.setParentId(0);
        categoryParams.setGrade(0);
        syncCategory(categoryParams);
    }

    public void syncCategory(CategoryParams categoryParams) {
        System.out.println(categoryParams.getParentId() + "  " + categoryParams.getGrade());
        String retJson = jdUnionService.getGoodsCategory(categoryParams);
        System.out.println(retJson);
        System.out.println("====================");
        JSONObject jsonObject = JSONObject.parseObject(retJson);
        Integer code = (Integer) jsonObject.get("code");
        String message = (String) jsonObject.get("message");
//        System.out.println("code=" + code);
//        System.out.println("message=" + message);
        if (code == 200) {
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");
            if (jsonArray != null) {
                System.out.println(jsonArray.size());
                for (Object object : jsonArray) {
                    String objJson = JSONObject.toJSONString(object);
                    JDCategoryESMap jdCategoryESMap = JSONObject.parseObject(objJson, JDCategoryESMap.class);
                    jdCategoryESService.save(jdCategoryESMap.getId(), jdCategoryESMap);
                    //next
                    if (jdCategoryESMap != null && !StringUtils.isNull(jdCategoryESMap.getId())) {
                        CategoryParams next_categoryParams = categoryParams;
                        int _c_grade = Integer.parseInt(jdCategoryESMap.getGrade()) + 1;
                        int _c_parentId = Integer.parseInt(jdCategoryESMap.getId());
                        if (_c_grade < 3) {
                            next_categoryParams.setParentId(_c_parentId);
                            next_categoryParams.setGrade(_c_grade);
                            syncCategory(next_categoryParams);
                        }
                    }
                }
            }
        }
    }

    public void loadAllCategory2Map(int page) {


        int now_count = OnLineService.getJdCategoryMap().size();
        if (page < 2 && now_count > 1) {
        } else {
            if (page < 0) {
                page = 1;
            }
            int size = 200;
            SearchHits hits = jdCategoryESService.findAll(page, size);
            long total = hits.getTotalHits().value;
            SearchHit[] hitList = hits.getHits();
            for (SearchHit hit : hitList) {
                String json = hit.getSourceAsString();
                JDCategoryESMap jdCategoryESMap = JSONObject.parseObject(json, JDCategoryESMap.class);
                OnLineService.addJdCategory(jdCategoryESMap.getId(), jdCategoryESMap);
            }
            int _count = OnLineService.getJdCategoryMap().size();
            long count = Integer.parseInt(_count + "");
            System.out.println("loadAllCategory2Map:" + count + "  total:" + total);
            if (total > count) {
                int nextPage = page + 1;
                loadAllCategory2Map(nextPage);
            }
        }
    }
}
