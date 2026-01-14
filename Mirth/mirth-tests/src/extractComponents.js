// msgField is the object corresponding to msg[segment][field]
export const extractComponents = (msgField, fieldName) => {
  const components = [];

  for (let i = 1; i <= 5; i++) {
    if (msgField && msgField[fieldName + "." + i] != null) {
      components.push(msgField[fieldName + "." + i].toString());
    } else {
      components.push("");
    }
  }

  return components.filter((x) => x !== "").join("^");
};
