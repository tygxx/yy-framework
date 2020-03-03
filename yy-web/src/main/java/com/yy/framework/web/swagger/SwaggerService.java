package com.yy.framework.web.swagger;

import com.yy.framework.web.swagger.vo.ApiDocument;
import com.yy.framework.web.swagger.vo.ApiOperation;
import com.yy.framework.web.swagger.vo.ApiTag;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SwaggerService {
	
	@Resource
	private DocumentationCache documentationCache;
	
	@Resource
	private ServiceModelToSwagger2Mapper mapper;
	
	/**
	 * 获取API的数据
	 * @return
	 */
	public ApiDocument getAllTags(){
		Documentation documentation = documentationCache.documentationByGroup(Docket.DEFAULT_GROUP_NAME);
		Swagger swagger = mapper.mapDocumentation(documentation);
		
		List<Tag> tags = swagger.getTags();
		Map<String, ApiTag> apiTagMap = new HashMap<>();
		for (Tag tag : tags) {
			ApiTag category = new ApiTag(tag);
			apiTagMap.put(category.getName(), category);
		}
		
		Map<String, Path> paths = swagger.getPaths();
		for (String url : paths.keySet()) {
			Path path = paths.get(url);
			Operation operation = path.getPost();
			if(operation != null) {
				ApiOperation apiOperation = new ApiOperation(url, HttpMethod.POST.name(), operation);
				List<String> operationTags = operation.getTags();
				for (String t : operationTags) {
					ApiTag category = apiTagMap.get(t);
					category.addOperations(apiOperation);
				}
			}
			
			operation = path.getGet();
			if(operation != null) {
				ApiOperation apiOperation = new ApiOperation(url, HttpMethod.GET.name(), operation);
				List<String> operationTags = operation.getTags();
				for (String t : operationTags) {
					ApiTag category = apiTagMap.get(t);
					category.addOperations(apiOperation);
				}
			}
			
			operation = path.getDelete();
			if(operation != null) {
				ApiOperation apiOperation = new ApiOperation(url, HttpMethod.DELETE.name(), operation);
				List<String> operationTags = operation.getTags();
				for (String t : operationTags) {
					ApiTag category = apiTagMap.get(t);
					category.addOperations(apiOperation);
				}
			}
			
			operation = path.getPut();
			if(operation != null) {
				ApiOperation apiOperation = new ApiOperation(url, HttpMethod.PUT.name(), operation);
				List<String> operationTags = operation.getTags();
				for (String t : operationTags) {
					ApiTag category = apiTagMap.get(t);
					category.addOperations(apiOperation);
				}
			}
		}
		
		List<ApiTag> apiTags = new ArrayList<>();
		for (String key : apiTagMap.keySet()) {
			apiTags.add(apiTagMap.get(key));
		}
		
		ApiDocument apiDocument = new ApiDocument();
		apiDocument.setInfo(swagger.getInfo());
		apiDocument.setApiTags(apiTags);
		apiDocument.setHost(swagger.getHost());
		apiDocument.setBasePath(swagger.getBasePath());
		return apiDocument;
	}
	
	/**
	 * API生成word文档的xml
	 * @return
	 */
	public String getWord() {
		ApiDocument apiDocument = this.getAllTags();
		Document document = getDocument();
		
		Element tagListNode = (Element) document.selectSingleNode("w:wordDocument/w:body/wx:sect/wx:sub-section[@array=\"apiTags\"]");
		Element parent = tagListNode.getParent();
		parent.remove(tagListNode);
		parent.addElement("api-list");
//		System.out.println(document.asXML());
		
		tagListNode.addNamespace("wx", "http://schemas.microsoft.com/office/word/2003/auxHint");
		tagListNode.addNamespace("w", "http://schemas.microsoft.com/office/word/2003/wordml");
		Element operationListNode = (Element) tagListNode.selectSingleNode("wx:sub-section[@array=\"api.operations\"]");
		parent = operationListNode.getParent();
		parent.remove(operationListNode);
		parent.addElement("operation-list");
//		System.out.println(tagListNode.asXML());
		
		operationListNode.addNamespace("wx", "http://schemas.microsoft.com/office/word/2003/auxHint");
		operationListNode.addNamespace("w", "http://schemas.microsoft.com/office/word/2003/wordml");
		Element parametersListNode = (Element) operationListNode.selectSingleNode("w:tbl/w:tr[@array=\"operation.parameters\"]");
		parent = parametersListNode.getParent();
		parent.remove(parametersListNode);
		parent.addElement("parameters-list");
//		System.out.println(operationListNode.asXML());
//		System.out.println(parametersListNode.asXML());
		
		StringBuffer tagSb = new StringBuffer(); 
		List<ApiTag> apiTags = apiDocument.getApiTags();
		for (ApiTag apiTag : apiTags) {
			String ts = tagListNode.asXML();
			ts = ts.replaceAll("\\$\\{api\\.name\\}", apiTag.getName());
			ts = ts.replaceAll("\\$\\{api\\.description\\}", apiTag.getDescription());
			StringBuffer operationSb = new StringBuffer(); 
			for (ApiOperation operation : apiTag.getOperations()) {
				String os = operationListNode.asXML();
				os = os.replaceAll("\\$\\{operation\\.summary\\}", operation.getSummary());
				os = os.replaceAll("\\$\\{operation\\.description\\}", StringUtils.isBlank(operation.getDescription()) ? "" : operation.getDescription());
				os = os.replaceAll("\\$\\{operation\\.url\\}", operation.getUrl());
				os = os.replaceAll("\\$\\{operation\\.method\\}", operation.getMethod());
				
				StringBuffer parameterSb = new StringBuffer(); 
				for (Parameter parameter : operation.getParameters()) {
					String ps = parametersListNode.asXML();
					if(parameter instanceof AbstractSerializableParameter) {
						AbstractSerializableParameter param = (AbstractSerializableParameter) parameter;
						ps = ps.replaceAll("\\$\\{param\\.getName\\(\\)\\}", param.getName());
						ps = ps.replaceAll("\\$\\{param\\.type\\}", StringUtils.isBlank(param.getType()) ? "" : param.getType());
						ps = ps.replaceAll("\\$\\{param\\.getRequired\\(\\)\\}", String.valueOf(param.getRequired()));
						ps = ps.replaceAll("\\$\\{param\\.defaultValue\\}", param.getDefaultValue() == null ? "" : param.getDefaultValue().toString());
						ps = ps.replaceAll("\\$\\{param\\.maxLength\\}", param.getMaxLength() == null ? "" : String.valueOf(param.getMaxLength()));
						ps = ps.replaceAll("\\$\\{param\\.getDescription\\(\\)\\}", StringUtils.isBlank(param.getDescription()) ? "" : param.getDescription());
					}else if(parameter instanceof BodyParameter) {
						BodyParameter param = (BodyParameter) parameter;
						ps = ps.replaceAll("\\$\\{param\\.getName\\(\\)\\}", StringUtils.isBlank(param.getName()) ? "" : param.getName());
						ps = ps.replaceAll("\\$\\{param\\.type\\}", StringUtils.isBlank(param.getSchema().getTitle()) ? "" : param.getSchema().getTitle());
						ps = ps.replaceAll("\\$\\{param\\.getRequired\\(\\)\\}", String.valueOf(param.getRequired()));
						ps = ps.replaceAll("\\$\\{param\\.defaultValue\\}", "");
						ps = ps.replaceAll("\\$\\{param\\.maxLength\\}", "");
						ps = ps.replaceAll("\\$\\{param\\.getDescription\\(\\)\\}", StringUtils.isBlank(param.getDescription()) ? "" : param.getDescription());
					}
					parameterSb.append(ps);
				}
				os = os.replaceAll("\\<parameters\\-list\\/>", parameterSb.toString());
				operationSb.append(os);
			}
			ts = ts.replaceAll("\\<operation\\-list\\/>", operationSb.toString());
			tagSb.append(ts);
		}
		String xml = document.asXML();
		xml = xml.replaceAll("\\$\\{info\\.getTitle\\(\\)\\}", StringUtils.isBlank(apiDocument.getInfo().getTitle()) ? "" : apiDocument.getInfo().getTitle());
		xml = xml.replaceAll("\\$\\{info\\.getDescription\\(\\)\\}", StringUtils.isBlank(apiDocument.getInfo().getDescription()) ? "" : apiDocument.getInfo().getDescription());
		xml = xml.replaceAll("\\$\\{info\\.getVersion\\(\\)\\}", StringUtils.isBlank(apiDocument.getInfo().getVersion()) ? "" : apiDocument.getInfo().getVersion());
		xml = xml.replaceAll("\\<api\\-list\\/>", tagSb.toString());
		return xml;
	}
	
	private Document getDocument() {
		Document document = null;
		try {
			SAXReader reader = new SAXReader();
			document = reader.read(this.getClass().getResourceAsStream("/static/swagger.xml"));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return document;
	}

}
