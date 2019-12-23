package com.power.doc.builder;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.power.common.util.FileUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.model.ApiMethodDoc;
import com.power.doc.model.ApiReqHeader;
import com.power.doc.model.postman.InfoBean;
import com.power.doc.model.postman.ItemBean;
import com.power.doc.model.postman.RequestItem;
import com.power.doc.model.postman.request.RequestBean;
import com.power.doc.model.postman.request.body.BodyBean;
import com.power.doc.model.postman.request.body.FormData;
import com.power.doc.model.postman.request.header.HeaderBean;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * @author yu 2019/11/21.
 */
public class PostmanJsonBuilder {

    /**
     * 构建postman json
     *
     * @param config 配置文件
     */
    public static void buildPostmanApi(ApiConfig config) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        JavaProjectBuilder javaProjectBuilder = new JavaProjectBuilder();
        SourceBuilders sourceBuilder = new SourceBuilders(config, javaProjectBuilder);
        List<ApiDoc> apiDocList = sourceBuilder.getControllerApiData();

        RequestItem requestItem = new RequestItem();
        requestItem.setInfo(new InfoBean(config.getProjectName()));
        List<ItemBean> itemBeans = new ArrayList<>();
        apiDocList.forEach(
                apiDoc -> {
                    ItemBean itemBean = buildItemBean(apiDoc);
                    itemBeans.add(itemBean);
                }
        );
        requestItem.setItem(itemBeans);
        String filePath = config.getOutPath();
        filePath = filePath + DocGlobalConstants.POSTMAN_JSON;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String data = gson.toJson(requestItem);
        FileUtil.nioWriteFile(data, filePath);
    }

    /**
     * 第一层的Item
     *
     * @param apiDoc
     * @return
     */
    private static ItemBean buildItemBean(ApiDoc apiDoc) {
        ItemBean itemBean = new ItemBean();
        itemBean.setName(apiDoc.getDesc());
        List<ItemBean> itemBeans = new ArrayList<>();
        List<ApiMethodDoc> apiMethodDocs = apiDoc.getList();
        apiMethodDocs.forEach(
                apiMethodDoc -> {
                    ItemBean itemBean1 = buildItem(apiMethodDoc);
                    itemBeans.add(itemBean1);
                }
        );
        itemBean.setItem(itemBeans);
        return itemBean;
    }

    /**
     * 构建第二层的item
     *
     * @param apiMethodDoc
     * @return
     */
    private static ItemBean buildItem(ApiMethodDoc apiMethodDoc) {
        ItemBean item = new ItemBean();
        RequestBean requestBean = new RequestBean();

        item.setName(apiMethodDoc.getDesc());
        item.setDescription(apiMethodDoc.getDetail());

        requestBean.setDescription(apiMethodDoc.getDesc());
        requestBean.setMethod(apiMethodDoc.getType());
        requestBean.setUrl(apiMethodDoc.getUrl());
        requestBean.setHeader(buildHeaderBeanList(apiMethodDoc));
        if (apiMethodDoc.getType().equals(DocGlobalConstants.HTTP_POST)) {
            requestBean.setBody(buildBodyBean(apiMethodDoc));
        } else {
            if (StringUtils.isNotBlank(apiMethodDoc.getRequestUsage()) &&
                    apiMethodDoc.getRequestUsage().startsWith("http")) {
                requestBean.setUrl(apiMethodDoc.getRequestUrlParam());
            }
        }
        item.setRequest(requestBean);
        return item;

    }

    /**
     * 构造请求体
     *
     * @param apiMethodDoc
     * @return
     */
    private static BodyBean buildBodyBean(ApiMethodDoc apiMethodDoc) {

        if (apiMethodDoc.getContentType().equals(DocGlobalConstants.POSTMAN_MODE_FORMDATA)) {
            BodyBean bodyBean = new BodyBean(true);
            bodyBean.setMode(DocGlobalConstants.POSTMAN_MODE_FORMDATA);
            String formData = apiMethodDoc.getRequestBody();

            Type type = new TypeToken<List<FormData>>(){}.getType();
            List<FormData> list = new Gson().fromJson(formData,type);
            bodyBean.setFormdata(list);

            return bodyBean;
        } else if (apiMethodDoc.getContentType().contains(DocGlobalConstants.APPLICATION_JSON)) {
            BodyBean bodyBean = new BodyBean(false);
            bodyBean.setMode(DocGlobalConstants.POSTMAN_MODE_RAW);
            bodyBean.setRaw(apiMethodDoc.getRequestBody());
            return bodyBean;
        } else {
            return new BodyBean(false);
        }

    }

    /**
     * 构造请求头
     *
     * @param apiMethodDoc
     * @return
     */
    private static List<HeaderBean> buildHeaderBeanList(ApiMethodDoc apiMethodDoc) {
        List<HeaderBean> headerBeans = new ArrayList<>();

        List<ApiReqHeader> headers = apiMethodDoc.getRequestHeaders();
        headers.forEach(
                apiReqHeader -> {
                    HeaderBean headerBean = new HeaderBean();
                    headerBean.setKey(apiReqHeader.getName());
                    headerBean.setName(apiReqHeader.getName());
                    headerBean.setDisabled(!apiReqHeader.isRequired());
                    headerBean.setDescription(apiReqHeader.getDesc());
                    headerBeans.add(headerBean);
                }
        );

        return headerBeans;
    }

}
