package org.kungfu.generator.bjui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.kungfu.core.Constants;
import org.kungfu.generator.TableMeta;

import com.jfinal.kit.StrKit;

/**
 * Controller 生成器
 */
public class ControllerGenerator {
	
	protected String packageTemplate =
			"package %s.%s;%n%n";
	protected String importTemplate =
			//"import java.util.Date;%n%n" +
			"import com.jfinal.plugin.activerecord.Page;%n%n" +
			"import org.kungfu.core.Controller;%n%n" +
			//"import %s.ext.render.BjuiRender;%n" +
			"import %s.%s.%s;%n%n";
	protected String classDefineTemplate =
		"/**%n" +
		" * %sController, Generated by Robot on " + Constants.DATE_TIME + ".%n" +
		" */%n" +
		"public class %sController extends Controller {%n%n" +
		"\tprivate final String MODULE_NAME = \"/%s\";%n%n" +
		"\tprivate %sService %sService = new %sService();%n%n";
	
	protected String indexTemplate =
			"%n\tpublic void index() {%n" +
			"\t\tPage<%s> page = %sService.page(getParaToInt(\"pageNumber\", 1), getParaToInt(\"pageSize\", 10));%n" +
			"\t\tsetAttr(\"page\", page);%n" +
			"\t}%n%n";
	
	protected String dateTimeTemplate = "\t\t\tmodel.set%d(new Date());%n";
	
	protected String saveTemplate =
			"\tpublic void save() {%n" +
			"\t\t%s model = getModel(%s.class, \"\");%n" +
			"\t\tboolean isSave = model.getId() == null;%n%n" +
			"\t\tif (%sService.saveOrUpdate(model, isSave))%n" +
			"\t\t\tredirect(MODULE_NAME);%n" +
			"\t\telse%n" +
			"\t\t\trenderError(501);%n" +
			"\t}%n%n";
	
	protected String editTemplate =
			"\tpublic void edit() {%n" +
			"\t\t%s %s = %sService.findById(getParaToInt(0,0));%n" +
			"\t\tif (%s == null)%n" +
			"\t\t\t%s = new %s();%n%n" +
			"\t\tsetAttr(\"%s\", %s);%n" +
			"\t}%n%n";
	
	protected String deleteTemplate =
			"\tpublic void delete() {%n%n" +
			"\t\t%sService.delete(getPara(0));%n%n" +
			"\t\tredirect(MODULE_NAME);%n" +
			"\t}%n%n";

	
	protected String modelPackageName;
	protected String baseModelPackageName;
	protected String modelOutputDir;
	protected boolean generateDaoInModel = true;
	
	public ControllerGenerator(String modelPackageName, String baseModelPackageName, String modelOutputDir) {
		if (StrKit.isBlank(modelPackageName))
			throw new IllegalArgumentException("modelPackageName can not be blank.");
		if (modelPackageName.contains("/") || modelPackageName.contains("\\"))
			throw new IllegalArgumentException("modelPackageName error : " + modelPackageName);
		if (StrKit.isBlank(baseModelPackageName))
			throw new IllegalArgumentException("baseModelPackageName can not be blank.");
		if (baseModelPackageName.contains("/") || baseModelPackageName.contains("\\"))
			throw new IllegalArgumentException("baseModelPackageName error : " + baseModelPackageName);
		if (StrKit.isBlank(modelOutputDir))
			throw new IllegalArgumentException("modelOutputDir can not be blank.");
		
		this.modelPackageName = modelPackageName;
		this.baseModelPackageName = baseModelPackageName;
		this.modelOutputDir = modelOutputDir;
	}
	
	public void setGenerateDaoInModel(boolean generateDaoInModel) {
		this.generateDaoInModel = generateDaoInModel;
	}
	
	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate Controller ...");
		for (TableMeta tableMeta : tableMetas)
			genModelContent(tableMeta);
		wirtToFile(tableMetas);
	}
	
	protected void genModelContent(TableMeta tableMeta) {
		StringBuilder ret = new StringBuilder();
		genPackage(tableMeta, ret);
		genImport(tableMeta, ret);
		genClassDefine(tableMeta, ret);
		genIndexMethod(tableMeta, ret);
		genSaveMethod(tableMeta, ret);
		genEditMethod(tableMeta, ret);
		//genDeleteMethod(tableMeta, ret);
		ret.append(String.format("}%n"));
		tableMeta.modelContent = ret.toString();
	}
	
	protected void genPackage(TableMeta tableMeta, StringBuilder ret) {
		ret.append(String.format(packageTemplate, modelPackageName, tableMeta.modelName.toLowerCase().replaceAll("_", "")));
	}
	
	protected void genImport(TableMeta tableMeta, StringBuilder ret) {
		ret.append(String.format(importTemplate, /*modelPackageName.subSequence(0, modelPackageName.lastIndexOf('.')), */modelPackageName, tableMeta.modelName.toLowerCase().replaceAll("_", ""), tableMeta.modelName));
	}
	
	protected void genClassDefine(TableMeta tableMeta, StringBuilder ret) {
		ret.append(String.format(classDefineTemplate, tableMeta.modelName, tableMeta.modelName, tableMeta.modelName.toLowerCase(), tableMeta.modelName, StrKit.firstCharToLowerCase(tableMeta.modelName), tableMeta.modelName));
	}
	
	protected void genIndexMethod(TableMeta tableMeta, StringBuilder ret) {
		ret.append(String.format(indexTemplate, tableMeta.modelName, StrKit.firstCharToLowerCase(tableMeta.modelName)));
	}
	
	/*private boolean fillDateTimeColumn(TableMeta tableMeta) {
		for (ColumnMeta columnMeta : tableMeta.columnMetas) 
			if (columnMeta.name.contains("Time") || columnMeta.name.contains("Date")) 
				return true;
		return false;
	}*/
	
	protected void genSaveMethod(TableMeta tableMeta, StringBuilder ret) {
		//if (fillDateTimeColumn(tableMeta))
			
		ret.append(String.format(saveTemplate, tableMeta.modelName, tableMeta.modelName, StrKit.firstCharToLowerCase(tableMeta.modelName)));
	}
	
	protected void genEditMethod(TableMeta tableMeta, StringBuilder ret) {
		ret.append(String.format(editTemplate, tableMeta.modelName, StrKit.firstCharToLowerCase(tableMeta.modelName), StrKit.firstCharToLowerCase(tableMeta.modelName), StrKit.firstCharToLowerCase(tableMeta.modelName), StrKit.firstCharToLowerCase(tableMeta.modelName), tableMeta.modelName, StrKit.firstCharToLowerCase(tableMeta.modelName), StrKit.firstCharToLowerCase(tableMeta.modelName)));
	}
	
	protected void genDeleteMethod(TableMeta tableMeta, StringBuilder ret) {
		ret.append(String.format(deleteTemplate, StrKit.firstCharToLowerCase(tableMeta.modelName)));
	}
	
	protected void wirtToFile(List<TableMeta> tableMetas) {
		try {
			for (TableMeta tableMeta : tableMetas)
				wirtToFile(tableMeta);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 若 model 文件存在，则不生成，以免覆盖用户手写的代码
	 */
	protected void wirtToFile(TableMeta tableMeta) throws IOException {
		File dir = new File(modelOutputDir + File.separator + tableMeta.modelName.toLowerCase().replaceAll("_", "") );
		if (!dir.exists())
			dir.mkdirs();
		
		String target = modelOutputDir + File.separator + tableMeta.modelName.toLowerCase().replaceAll("_", "") + File.separator + tableMeta.modelName + "Controller.java";
		
		File file = new File(target);
		if (file.exists()) {
			return ;	// 若 Model 存在，不覆盖
		}
		
		FileWriter fw = new FileWriter(file);
		try {
			fw.write(tableMeta.modelContent);
		}
		finally {
			fw.close();
		}
	}
}

