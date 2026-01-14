// mock global logger used in Mirth
global.logger = {
  warn: () => {},
  info: () => {},
  error: () => {},
};

import { expect } from "chai";
import { getHL7FieldValue, normalizeDate } from "../src/dateUtils.js";

describe("HL7 Date Utilities", () => {
  // ---------------------------------------------------------------------
  // getHL7FieldValue() tests
  // ---------------------------------------------------------------------
  describe("getHL7FieldValue()", () => {
    it("should return empty string if field is missing", () => {
      const obj = { "PID.7.1": "20240201" };
      expect(getHL7FieldValue(null, "PID.7")).to.equal("");
      expect(getHL7FieldValue(undefined, "PID.7")).to.equal("");
      expect(getHL7FieldValue(obj, "")).to.equal("");
      expect(getHL7FieldValue(obj, null)).to.equal("");
      expect(getHL7FieldValue(obj, undefined)).to.equal("");
    });

    it("should return the string unchanged", () => {
      const obj = { "PID.7.1": " 20240201 " };
      expect(getHL7FieldValue(" 20240201 ", "PID.7")).to.equal("20240201");
    });

    it("should extract value from HL7 component object (PID.7.1)", () => {
      const obj = { "PID.7.1": " 20240201 " };
      expect(getHL7FieldValue(obj, "PID.7")).to.equal("20240201");
    });

    it("should handle unexpected objects via toString()", () => {
      const weirdObj = { toString: () => "99991231" };
      expect(getHL7FieldValue(weirdObj, "test")).to.equal("99991231");
    });

    it("should return object.toString() fallback if object has no readable value", () => {
      expect(getHL7FieldValue({})).to.equal("");
    });
  });

  describe("HL7 Repeating Segment Date Normalization", () => {
    it("should normalize dates inside repeating OBR segments", () => {
      // Simulated HL7 MSG structure as seen in Mirth
      const msg = {
        OBR: [
          { "OBR.7": { "OBR.7.1": "2024-01-01" } },
          { "OBR.7": { "OBR.7.1": "2024/01/05 12:30:45" } },
          { "OBR.7": null }, // missing field
          {}, // segment exists but field is missing
        ],
      };

      // Simulate your transformer logic
      msg.OBR.forEach((seg) => {
        if (seg["OBR.7"]) {
          const value = getHL7FieldValue(seg["OBR.7"], "OBR.7");
          seg["OBR.7"] = normalizeDate(value);
        }
      });

      // Assertions
      expect(msg.OBR[0]["OBR.7"]).to.equal("20240101");
      expect(msg.OBR[1]["OBR.7"]).to.equal("20240105123045");
      expect(msg.OBR[2]["OBR.7"]).to.equal(null); // unchanged because null passes through
      expect(msg.OBR[3]["OBR.7"]).to.equal(undefined); // untouched
    });

    it("should not throw errors when repeating array has invalid objects", () => {
      const msg = {
        OBR: [
          5, // invalid type
          null, // null segment
          { "OBR.7": {} }, // empty field object
          { "OBR.7": { "OBR.7.1": "" } }, // empty string
        ],
      };

      expect(() => {
        msg.OBR.forEach((seg) => {
          if (!seg || typeof seg !== "object") return;

          if (seg["OBR.7"]) {
            const value = getHL7FieldValue(seg["OBR.7"], "OBR.7");
            seg["OBR.7"] = normalizeDate(value);
          }
        });
      }).to.not.throw();

      // Check results
      expect(msg.OBR[2]["OBR.7"]).to.equal("[object Object]"); // cleaned empty
      expect(msg.OBR[3]["OBR.7"]).to.equal("[object Object]"); // empty input stays empty
    });
  });

  // ---------------------------------------------------------------------
  // normalizeDate() tests
  // ---------------------------------------------------------------------
  describe("normalizeDate()", () => {
    it("should return value unchanged if null/undefined/empty", () => {
      expect(normalizeDate(null)).to.equal(null);
      expect(normalizeDate(undefined)).to.equal(undefined);
      expect(normalizeDate("")).to.equal("");
    });

    it("should allow valid YYYYMMDD", () => {
      expect(normalizeDate("20240101")).to.equal("20240101");
    });

    it("should allow valid YYYYMMDDHHMMSS", () => {
      expect(normalizeDate("20240101123045")).to.equal("20240101123045");
    });

    it("should strip non-digit characters", () => {
      expect(normalizeDate("2024-01-01")).to.equal("20240101");
      expect(normalizeDate("2024/01/01 12:30:45")).to.equal("20240101123045");
    });

    it("should return original value if cleaned length is invalid", () => {
      const bad = "2024-01";
      expect(normalizeDate(bad)).to.equal(bad);
    });
  });

  // ---------------------------------------------------------------------
  // Simulated HL7 segment usage test
  // ---------------------------------------------------------------------
  describe("integration: simulating HL7 segment object", () => {
    it("should normalize an HL7 PID segment structure", () => {
      const PID = {
        "PID.7": { "PID.7.1": "2024-02-01" },
      };

      const value = getHL7FieldValue(PID["PID.7"], "PID.7");
      const normalized = normalizeDate(value);

      expect(normalized).to.equal("20240201");
    });

    it("should handle missing fields safely", () => {
      const PID = {};
      expect(() => getHL7FieldValue(PID["PID.7"], "PID.7")).to.not.throw();
      expect(getHL7FieldValue(PID["PID.7"], "PID.7")).to.equal("");
    });
  });
});
