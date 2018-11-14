package com.beetle.component.boot;

/*
 * 基于：<br>
 * https://github.com/hengyunabc/executable-embeded-tomcat-sample<br>
 * 修改，优化，支持最新的tomcat9.0.x,Henry 2018-11-14
 */
import java.io.IOException;

import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;

import com.beetle.component.boot.tomcat.EmbededContextConfig;
import com.beetle.component.boot.tomcat.EmbededStandardJarScanner;
import com.beetle.component.boot.tomcat.TomcatUtil;
import com.beetle.component.boot.tomcat.WebXmlMountListener;
import com.beetle.framework.AppProperties;
import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;

/**
 *
 * @author hengyunabc
 * @author henryyu <br>
 *         默认从application.properties获取项目服务器配置参数；<br>
 *         如果不想依赖application.properties配置文件，可以重载相关的getXXX参数方法
 */
public class BJAFBoot {
	private static final Logger logger = AppLogger.getLogger(BJAFBoot.class);

	protected String getHostname() {
		return AppProperties.get("tomcat_host", "localhost");
	}

	protected int getPort() {
		return AppProperties.getAsInt("tomcat_port", 8080);
	}

	protected String getTomcatDir() {
		return AppProperties.get("tomcat_dir", "tomcat");
	}

	protected String getTomcatWebappsDir() {
		return AppProperties.get("tomcat_webapps_dir", "webapps");
	}

	protected String getTomcatWarDir() {
		return AppProperties.get("tomcat_wardir", "ROOT");
	}

	public void start() throws IOException {
		// 一个是baseDir，对应tomcat本身的目录，下面有conf, bin这些文件夹
		String tomcatBaseDir = TomcatUtil.getDir(this.getTomcatDir()).getAbsolutePath();
		logger.info("tomcatDir:{}", tomcatBaseDir);
		// webApps
		String appdir[] = getTomcatWarDir().split(";");
		Tomcat tomcat = new Tomcat();
		tomcat.setBaseDir(tomcatBaseDir);
		tomcat.setPort(getPort());
		tomcat.setHostname(getHostname());
		tomcat.getConnector();// tomcat9.x必须执行这个，否则绑定不了本地地址，坑爹
		Host host = tomcat.getHost();
		String webappsPath = TomcatUtil.getDir(tomcatBaseDir + "/" + getTomcatWebappsDir()).getAbsolutePath();
		for (int i = 0; i < appdir.length; i++) {
			String contextPath = appdir[i];
			if (contextPath.equals("") || contextPath.equalsIgnoreCase("root")) {
				contextPath = "";
			}
			logger.info("war(contextPath):{}", contextPath);
			String contextDocBase = TomcatUtil.getDir(webappsPath + "/" + contextPath).getAbsolutePath();
			logger.info("war(contextDocBase):{}", contextDocBase);
			Context context = tomcat.addWebapp(host, contextPath, contextDocBase, new EmbededContextConfig());
			context.setJarScanner(new EmbededStandardJarScanner());
			ClassLoader classLoader = BJAFBoot.class.getClassLoader();
			context.setParentClassLoader(classLoader);
			// context load WEB-INF/web.xml from classpath
			context.addLifecycleListener(new WebXmlMountListener());
		}
		try {
			tomcat.start();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("tomcat start err", e);
		}
		tomcat.getServer().await();
		logger.info("tomcat stoped");
	}

	public static void main(String[] args) {
		try {
			new BJAFBoot().start();
		} catch (Exception e) {
			logger.error("tomcat start err", e);
			throw new AppRuntimeException(e);// 终止应用
		}
	}
}
