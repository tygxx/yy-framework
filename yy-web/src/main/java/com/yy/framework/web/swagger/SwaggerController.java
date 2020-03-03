package com.yy.framework.web.swagger;

import com.yy.framework.core.response2.ResponseExecutable;
import com.yy.framework.core.response2.ResponseHolder;
import com.yy.framework.core.response2.RestResponse;
import com.yy.framework.web.swagger.vo.ApiDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping(value = "/swagger")
@ApiIgnore
public class SwaggerController {
	
	private final Logger logger = LoggerFactory.getLogger(SwaggerController.class);
	
	@Resource
	private SwaggerService swaggerService;
	
	@ApiIgnore
	@ResponseBody
	@RequestMapping(value = "api/rest", method = RequestMethod.GET)
    public RestResponse<ApiDocument> api(){
		return ResponseHolder.build(new ResponseExecutable<ApiDocument>() {
			@Override
			public ApiDocument execute() {
				return swaggerService.getAllTags();
			}
		});
		
//        return new ResponseCallBack() {
//			@Override
//			public void execute(ResponseCriteria criteria, Object... obj) {
//				criteria.addSingleResult(swaggerService.getAllTags());
//			}
//		}.sendRequest();
    }
	
	@ApiIgnore
	@RequestMapping(value = "api/word", method = RequestMethod.GET)
    public void wrod(HttpServletRequest request, HttpServletResponse response){
		response.setHeader("content-Type", "application/msword;charset=utf-8");
	    response.setHeader("Content-Disposition", "attachment;filename=api.doc");
	    String word = null;
		try {
			word = swaggerService.getWord();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	    OutputStream out = null;
		try {
			out = response.getOutputStream();
			out.write(word.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("浏览文件异常", e);
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }
	
}
