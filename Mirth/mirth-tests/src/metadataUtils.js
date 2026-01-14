//metadataUtils.js

// Returns the HL7 message type, e.g., "ORU^R01"
export const getMessageType = (msg) => {
  if (!msg || !msg["MSH"] || !msg["MSH"]["MSH.9"]) return "";
  const seg = msg["MSH"]["MSH.9"];
  return (
    seg["MSH.9.1"].toString().trim() + "^" + seg["MSH.9.2"].toString().trim()
  );
};

// Counts segments (OBX, NTE, etc.)
export const countSegments = (msg, segmentName) => {
  if (!msg || !msg[segmentName]) return 0;
  return Object.keys(msg[segmentName]).length;
};
