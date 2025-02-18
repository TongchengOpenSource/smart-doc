/*
 * smart-doc
 *
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

package com.ly.doc.utils;

import java.util.function.BiConsumer;

/**
 * Iterables
 *
 * @author daiww
 */
public class Iterables {

	/**
	 * private constructor
	 */
	private Iterables() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * forEach
	 * @param elements elements
	 * @param action action
	 * @param <E> E
	 */
	public static <E> void forEach(Iterable<? extends E> elements, BiConsumer<Integer, ? super E> action) {
		if (elements == null || action == null) {
			return;
		}
		int index = 0;
		for (E element : elements) {
			action.accept(index++, element);
		}
	}

}
