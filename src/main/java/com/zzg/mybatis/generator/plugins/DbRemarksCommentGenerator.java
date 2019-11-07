/*
 *  Copyright 2008 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.zzg.mybatis.generator.plugins;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.InnerEnum;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * 此插件使用数据库表中列的注释来生成Java Model中属性的注释
 *
 * @author Owen Zou
 * 
 */
public class DbRemarksCommentGenerator implements CommentGenerator {

	private Properties properties;
	private boolean isAnnotations;

	public DbRemarksCommentGenerator() {
		super();
		properties = new Properties();
	}

	/**
	 * java import 导包
	 */
	public void addJavaFileComment(CompilationUnit compilationUnit) {
		// add no file level comments by default
		if (isAnnotations) {
			if (compilationUnit.getType().getShortName().endsWith("Example")) {// 不给example导包
				compilationUnit.addImportedType(new FullyQualifiedJavaType("java.io.Serializable"));
				if (compilationUnit instanceof InnerClass) {
					this.addSerialVersionUID((InnerClass) compilationUnit);
					this.addClassComment((InnerClass) compilationUnit);
				}
				return;
			}
			compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.persistence.Table"));
			compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.persistence.Id"));
			compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.persistence.Column"));
			compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.persistence.GeneratedValue"));
			compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.validation.constraints.NotEmpty"));
			compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.persistence.GenerationType"));
			// TOOD 可添加swagger注解
		}
	}

	/**
	 * Adds a suitable comment to warn users that the element was generated, and
	 * when it was generated.
	 */
	public void addComment(XmlElement xmlElement) {
	}

	public void addRootComment(XmlElement rootElement) {
		// add no document level comments by default
	}

	public void addConfigurationProperties(Properties properties) {
		this.properties.putAll(properties);
		isTrue(properties.getProperty("columnRemarks"));
		isAnnotations = isTrue(properties.getProperty("annotations"));
	}

	public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
		addClassComment(innerClass);
		addSerialVersionUID(innerClass);
	}

	/**
	 * 实体类添加注释
	 */
	public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		addClassComment(topLevelClass);
		if (isAnnotations) {
			topLevelClass
					.addAnnotation("@Table(name=\"" + introspectedTable.getFullyQualifiedTableNameAtRuntime() + "\")");
		}
	}

	public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
	}

	public void addFieldComment(Field field, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
			// field.addJavaDocLine("/**");
			// StringBuilder sb = new StringBuilder();
			// sb.append(" * ");
			// sb.append(introspectedColumn.getRemarks());
			// field.addJavaDocLine(sb.toString());
			// field.addJavaDocLine(" */");

			field.addJavaDocLine(MessageFormat.format("/**{0}**/", introspectedColumn.getRemarks()));
		}

		if (isAnnotations) {
			boolean isId = false;
			for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
				if (introspectedColumn == column) {
					isId = true;
					field.addAnnotation("@Id");
					break;
				}
			}

			if (!introspectedColumn.isNullable() && !isId) {
				field.addAnnotation("@NotEmpty");// 给非空字段添加验证注解
			}
			// 给字段添加Columu 属性
			field.addAnnotation(
					MessageFormat.format("@Column(name=\"{0}\")", introspectedColumn.getActualColumnName()));

			// 给注解字段添加注解
			if (introspectedColumn.isIdentity()) {
				if (introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement()
						.equals("JDBC")) {
					field.addAnnotation("@GeneratedValue(generator = \"JDBC\")");
				} else {
					field.addAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY)");
				}
			} else if (introspectedColumn.isSequenceColumn()) {
				field.addAnnotation("@SequenceGenerator(name=\"\",sequenceName=\""
						+ introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement() + "\")");
			}
		}
	}

	public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
	}

	public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
	}

	public void addGetterComment(Method method, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		String fieldComment = MessageFormat.format("/**{0}**/", introspectedColumn.getRemarks());
		method.addJavaDocLine(fieldComment);
	}

	public void addSetterComment(Method method, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		String fieldComment = MessageFormat.format("/**{0}**/", introspectedColumn.getRemarks());
		method.addJavaDocLine(fieldComment);
	}

	public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
		addClassComment(innerClass);

		addSerialVersionUID(innerClass);
	}

	private void addClassComment(InnerClass innerClass) {
		innerClass.addJavaDocLine("/**");
		innerClass.addJavaDocLine(" * @Description: TODO");// 描述信息
		innerClass.addJavaDocLine(" * @author CharlesXiong");
		innerClass.addJavaDocLine(" * @date: " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
		innerClass.addJavaDocLine(" * @see {@link TODO }");// link信息
		innerClass.addJavaDocLine(" */");
	}

	private void addSerialVersionUID(InnerClass innerClass) {
		FullyQualifiedJavaType superInterface = new FullyQualifiedJavaType("java.io.Serializable");// 此种不用手动额外导包
		innerClass.addSuperInterface(superInterface);
		// 添加 private static final long serialVersionUID = 1L;
		Field field = new Field();
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setStatic(true);
		field.setFinal(true);
		field.setType(new FullyQualifiedJavaType("long"));
		field.setName("serialVersionUID");
		field.setInitializationString("1L");
		innerClass.addField(field);
	}
}
