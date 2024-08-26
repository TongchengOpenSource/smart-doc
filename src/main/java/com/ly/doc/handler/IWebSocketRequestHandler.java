/*
 * Copyright (C) 2018-2024 smart-doc
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.ly.doc.handler;

import com.ly.doc.constants.DocAnnotationConstants;
import com.ly.doc.constants.DocTags;
import com.ly.doc.model.request.ServerEndpoint;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.AnnotationValueList;
import com.thoughtworks.qdox.model.expression.Constant;
import com.thoughtworks.qdox.model.expression.TypeRef;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * websocket handler
 *
 * @author linwumingshi
 */
public interface IWebSocketRequestHandler {

	/**
	 * handle class annotation `@ServerEndpoint`
	 * @param javaAnnotation javaAnnotation @ServerEndpoint
	 * @param cls JavaClass
	 * @return ServerEndpoint
	 */
	default ServerEndpoint handleServerEndpoint(JavaClass cls, JavaAnnotation javaAnnotation) {
		if (Objects.nonNull(cls.getTagByName(DocTags.IGNORE))) {
			return null;
		}
		ServerEndpoint builder = ServerEndpoint.builder();
		// get the value of JavaAnnotation
		Optional.ofNullable(javaAnnotation.getProperty(DocAnnotationConstants.VALUE_PROP))
			.map(Object::toString)
			.map(StringUtil::removeQuotes)
			.ifPresent(builder::setUrl);

		// get subProtocols of annotation
		builder.setSubProtocols(this.extractStringList(javaAnnotation, "subprotocols"));

		// Handle 'decoders' property
		this.getTypeList(javaAnnotation, "decoders").ifPresent(builder::setDecoders);

		// Handle 'encoders' property
		this.getTypeList(javaAnnotation, "encoders").ifPresent(builder::setEncoders);
		return builder;
	}

	/**
	 * Extracts a list of strings from an annotation property.
	 * @param javaAnnotation the annotation containing the property
	 * @param propertyName the name of the property
	 * @return a list of strings
	 */
	default List<String> extractStringList(JavaAnnotation javaAnnotation, String propertyName) {
		return Optional.ofNullable(javaAnnotation.getProperty(propertyName)).map(value -> {
			if (value instanceof AnnotationValueList) {
				return ((AnnotationValueList) value).getValueList()
					.stream()
					.map(Object::toString)
					.filter(StringUtil::isNotEmpty)
					.collect(Collectors.toList());
			}
			if (value instanceof Constant) {
				return Collections.singletonList(((Constant) value).getValue().toString());
			}
			return Collections.<String>emptyList();
		}).orElse(Collections.emptyList());
	}

	/**
	 * Retrieves a list of fully qualified type names from an annotation property.
	 * @param javaAnnotation the annotation containing the property
	 * @param propertyName the name of the property to retrieve
	 * @return a containing the list of type names if present
	 */
	default Optional<List<String>> getTypeList(JavaAnnotation javaAnnotation, String propertyName) {
		AnnotationValue annotationValue = javaAnnotation.getProperty(propertyName);
		if (Objects.isNull(annotationValue)) {
			return Optional.empty();
		}
		if (annotationValue instanceof AnnotationValueList) {
			List<String> valueList = ((AnnotationValueList) annotationValue).getValueList()
				.stream()
				.filter(i -> i instanceof TypeRef)
				.map(i -> ((TypeRef) i).getType().getFullyQualifiedName())
				.collect(Collectors.toList());
			return Optional.of(valueList);
		}
		if (annotationValue instanceof TypeRef) {
			return Optional
				.of(Collections.singletonList(((TypeRef) annotationValue).getType().getFullyQualifiedName()));
		}
		return Optional.empty();
	}

}
