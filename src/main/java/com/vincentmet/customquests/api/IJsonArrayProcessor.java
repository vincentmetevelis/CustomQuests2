package com.vincentmet.customquests.api;

import com.google.gson.JsonArray;
import com.vincentmet.customquests.api.exception.JsonValueTypeMismatch;

public interface IJsonArrayProcessor{
	void processJson(JsonArray json) throws JsonValueTypeMismatch;
}
