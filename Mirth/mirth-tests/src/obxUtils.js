// obxUtils.js

export const extractObxFields = (obx) => {
  //const {
  //  "OBX.3": { "OBX.3.1": nameField = "", "OBX.3.2": codeField = "" } = {},
  //  "OBX.5": { "OBX.5.1": valueField = "" } = {},
  //  "OBX.7": { "OBX.7.1": rangeField = "" } = {},
  //} = obx || {}; // fallback if obx is null/undefined
  //
  // !! The above doesnt work because of an error:
  // "Default values in destructuring declarations are not supported"
  //
  //return [
  //  nameField != null ? nameField.toString() : "",
  //  codeField != null ? codeField.toString() : "",
  //  valueField != null ? valueField.toString() : "",
  //  rangeField != null ? rangeField.toString() : "",
  //];

  obx = obx || {};
  var nameField = "";
  var codeField = "";
  var valueField = "";
  var rangeField = "";

  if (obx["OBX.3"]) {
    if (obx["OBX.3"]["OBX.3.1"] != null)
      nameField = obx["OBX.3"]["OBX.3.1"].toString();
    if (obx["OBX.3"]["OBX.3.2"] != null)
      codeField = obx["OBX.3"]["OBX.3.2"].toString();
  }

  if (obx["OBX.5"]) {
    if (obx["OBX.5"]["OBX.5.1"] != null)
      valueField = obx["OBX.5"]["OBX.5.1"].toString();
  }

  if (obx["OBX.7"]) {
    if (obx["OBX.7"]["OBX.7.1"] != null)
      rangeField = obx["OBX.7"]["OBX.7.1"].toString();
  }

  return [nameField, codeField, valueField, rangeField];
};

export const interpretResultStatus = (valueStr, rangeStr) => {
  if (!rangeStr || rangeStr.trim() === "") {
    return "status: UNDEFINED";
  }

  var parts = rangeStr.split("-");
  if (parts.length !== 2) {
    return "status: UNDEFINED";
  }

  var min = parseFloat(parts[0]);
  var max = parseFloat(parts[1]);
  var value = parseFloat(valueStr);

  if (isNaN(min) || isNaN(max) || isNaN(value)) {
    return "status: UNDEFINED";
  }

  if (value < min) return "status: LOW";
  if (value > max) return "status: HIGH";

  return "status: NORMAL";
};
