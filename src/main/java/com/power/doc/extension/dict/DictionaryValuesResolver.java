package com.power.doc.extension.dict;

import com.power.common.model.EnumDictionary;

import java.util.Collection;

public interface DictionaryValuesResolver {
    <T extends EnumDictionary> Collection<T> resolve();
}
