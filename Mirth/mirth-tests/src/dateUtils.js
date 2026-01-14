// dateUtils.js

// Helper to safely get the string value of an HL7 field
export const getHL7FieldValue = (fieldObj, fieldName) => {
  if (!fieldObj || !fieldName) return "";

  try {
    var component = fieldObj[fieldName + ".1"];
    if (component) {
      return component.toString().trim();
    }
    return fieldObj.toString().trim();
  } catch (e) {
    return "";
  }
};

export const normalizeDate = (value) => {
  if (!value) return value;

  // Remove non-digits
  var cleaned = value.replace(/\D/g, "");

  // Allow only YYYYMMDD or YYYYMMDDHHMMSS
  if (cleaned.length === 8 || cleaned.length === 14) {
    return cleaned;
  }

  // Otherwise, log and leave untouched
  logger.warn("Invalid date format: " + value);
  return value;
};
