package com.tedu.sp11;



import javax.servlet.http.HttpServletRequest;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.tedu.web.util.JsonResult;

@Component
public class AccessFilter extends ZuulFilter{

	@Override
	public boolean shouldFilter() {
		//判断当前请求是否应用当前过滤器
		//如果请求后台 item-service,执行当前过滤代码 
		RequestContext ctx = RequestContext.getCurrentContext();
		String serviceId = (String) ctx.get(FilterConstants.SERVICE_ID_KEY);
		
		if(serviceId.equals("item-service")) {
			return true;
		}
		
		return false;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		String token = request.getParameter("token");
		if(token == null || token.length() == 0) {
			//阻止继续执行,直接向客户端返回结果
			ctx.setSendZuulResponse(false);
			ctx.setResponseStatusCode(200);
			ctx.setResponseBody(JsonResult.err("not login").code(JsonResult.NOT_LOGIN).toString());
		}
		//zuul设计师考虑到以后的发展,添加了返回值
		//目前这个返回值,没用
		return null;
	}

	@Override
	public String filterType() {
		//返回过滤器类型,前置,后置,路由,错误
		return FilterConstants.PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		//该过滤器顺序要 > 5，才能得到 serviceid
		return FilterConstants.PRE_DECORATION_FILTER_ORDER+1;
	}

}
