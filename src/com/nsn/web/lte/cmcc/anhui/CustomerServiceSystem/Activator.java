package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.nsn.logger.Logger;
import com.nsn.web.SystemMenu;
import com.nsn.web.SystemMenuPath;
import com.nsn.web.lte.DoSystem;
import com.nsn.web.lte.DoWebApplication;
import com.nsn.web.lte.DoWebModule;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.action.CSSAction;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils.HBaseUtil;
import com.nsn.web.lte.db.DSType;
import com.nsn.web.lte.db.SqlMap;
import com.nsn.web.lte.mvc.Actions;


public class Activator implements BundleActivator {
	private Logger log = Logger.getLogger(this.getClass().getName());
	private static String packageName = Activator.class.getPackage().getName();
	
	private final static String MODULE_ID = "css";
	private final static String MODULE_NAME = "智能客服投诉";
	//private final static String ID = "/sys";
	
	
	@Override
	public void start(BundleContext context) throws Exception {
		//所属一级菜单
		String oneMenu = "智能客服投诉";	//一级菜单名，页面有自定义菜单功能，建议VoLTE模块默认这个一级菜单
		SystemMenuPath root = new SystemMenuPath().menu(new SystemMenu().index(0).id(oneMenu).name(oneMenu).clazz("glyphicon glyphicon-th-large"));
		//SystemMenu root = new SystemMenu().id(MODULE_ID).name(MODULE_NAME).clazz("glyphicon-cog");
		//模块路径，所在包的最后一个文件夹名，例如com.nsn.web.abcdef，moduleKey为/abcdef
		String dirName = packageName.substring(packageName.lastIndexOf(".") + 1);
		String moduleKey = "/" + dirName;
		System.out.println("moduleKey "+moduleKey);
		DoWebApplication webapp = new DoWebApplication(DoSystem.getWebSystem(), context.getBundle(),
				this.getClass().getClassLoader());
		webapp.setContextPath(moduleKey);
		
		//页面调用action的key
		String actionKey = moduleKey + "/CSSAction";	//页面调用action的key
		Actions.add(actionKey, CSSAction.class);
		DoSystem.getWebSystem().addWebApplication(webapp);
		
		//模块菜单入口
		String UserModuleName = "智能客服投诉";	//根据情况修改，模块菜单名，建议一个模块一个菜单
	
		SystemMenuPath userMenu = new SystemMenuPath(root.clone()).menu(new SystemMenu().id(actionKey).name(UserModuleName).icon("fa-file"));
		//SystemMenuPath userMenu =new SystemMenuPath().menu(root).next(true).menu(new SystemMenu().id(actionKey).name(UserModuleName).icon("fa-map"));
		DoWebModule userModule = new DoWebModule(webapp,actionKey,UserModuleName,userMenu).setModuleUrl(actionKey);
		webapp.addWebModule(userModule);
		SqlMap.loadSql(this.getClass(),"sql", DSType.MAIN);
		HBaseUtil.getOneRecord("LOAD_SESSION","LOAD_SESSION");
		log.info(packageName + " STARTED!");
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		log.info(packageName + " STOPPED.");
	}
}
