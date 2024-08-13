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
import com.thoughtworks.qdox.model.expression.TypeRef;

import java.util.List;
import java.util.Objects;
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
		AnnotationValue property = javaAnnotation.getProperty(DocAnnotationConstants.VALUE_PROP);
		// if value is not null
		if (Objects.nonNull(property)) {
			builder.setUrl(StringUtil.removeQuotes(property.toString()));
		}
		// get subProtocols of annotation
		AnnotationValue subProtocolsOfAnnotation = javaAnnotation.getProperty("subprotocols");
		if (Objects.nonNull(subProtocolsOfAnnotation) && subProtocolsOfAnnotation instanceof AnnotationValueList) {
			List<AnnotationValue> valueList = ((AnnotationValueList) subProtocolsOfAnnotation).getValueList();
			List<String> subProtocols = valueList.stream().map(Object::toString).collect(Collectors.toList());
			builder.setSubProtocols(subProtocols);
		}

		// get decoders of annotation
		AnnotationValue decodersOfAnnotation = javaAnnotation.getProperty("decoders");
		if (Objects.nonNull(decodersOfAnnotation) && decodersOfAnnotation instanceof AnnotationValueList) {
			List<AnnotationValue> valueList = ((AnnotationValueList) decodersOfAnnotation).getValueList();
			List<String> decoders = valueList.stream()
				.filter(i -> i instanceof TypeRef)
				.map(i -> ((TypeRef) i).getType().getFullyQualifiedName())
				.collect(Collectors.toList());
			builder.setDecoders(decoders);
		}

		// get encoders of annotation
		AnnotationValue encodersOfAnnotation = javaAnnotation.getProperty("encoders");
		if (Objects.nonNull(encodersOfAnnotation) && encodersOfAnnotation instanceof AnnotationValueList) {
			List<AnnotationValue> valueList = ((AnnotationValueList) encodersOfAnnotation).getValueList();
			List<String> encoders = valueList.stream()
				.filter(i -> i instanceof TypeRef)
				.map(i -> ((TypeRef) i).getType().getFullyQualifiedName())
				.collect(Collectors.toList());
			builder.setEncoders(encoders);
		}

		return builder;
	}

}
