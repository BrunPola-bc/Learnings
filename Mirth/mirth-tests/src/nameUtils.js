// nameUtils.js

// Capitalizes first letter of a string (name)
export const capitalize = (value) => {
  if (!value) return value;
  const lower = value.toLowerCase();
  return lower[0].toUpperCase() + lower.slice(1);
};

// Normalize a HL7 name field (family, given, middle)
export const normalizeName = (fieldName, field) => {
  if (!field) return;

  for (var i = 1; i <= 3; i++) {
    var key = fieldName + "." + i;
    if (field[key]) {
      field[key] = capitalize(field[key].toString());
    }
  }
};

// export default { capitalize, normalizeName };
