/*
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
package com.ly.doc.builder.rpc;

import com.ly.doc.builder.IRpcDocBuilderTemplate;
import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.constants.FrameworkEnum;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.rpc.RpcApiDoc;
import com.power.common.util.StringUtil;

/**
 * rpc doc builder template.
 *
 * @author yu 2020/5/16.
 */
public class RpcDocBuilderTemplate implements IRpcDocBuilderTemplate<RpcApiDoc> {

	@Override
	public void checkAndInit(ApiConfig config, boolean checkOutPath) {
		if (StringUtil.isEmpty(config.getFramework())) {
			config.setFramework(FrameworkEnum.DUBBO.getFramework());
		}
		IRpcDocBuilderTemplate.super.checkAndInit(config, checkOutPath);
		config.setOutPath(config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + DocGlobalConstants.RPC_OUT_DIR);
	}

	@Override
	public RpcApiDoc createEmptyApiDoc() {
		return new RpcApiDoc();
	}

}
