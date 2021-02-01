package com.vincentmet.customquests.api;

import com.google.gson.JsonPrimitive;
import com.vincentmet.customquests.api.exception.JsonValueTypeMismatch;

public interface IJsonPrimitiveProcessor{
	void processJson(JsonPrimitive json) throws JsonValueTypeMismatch;
}
