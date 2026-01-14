import { expect } from "chai";
import { extractObxFields, interpretResultStatus } from "../src/obxUtils.js";

describe("OBX Utilities", () => {
  describe("extractObxFields()", () => {
    it("should return values", () => {
      const obx = {
        "OBX.3": { "OBX.3.1": "Test Name", "OBX.3.2": "ABC123" },
        "OBX.5": { "OBX.5.1": "45.6" },
        "OBX.7": { "OBX.7.1": "40-50" },
      };

      const result = extractObxFields(obx);
      expect(result).to.deep.equal(["Test Name", "ABC123", "45.6", "40-50"]);
    });

    it("should handle missing (sub)fields gracefully", () => {
      const obx = {
        "OBX.3": { "OBX.3.1": "Name Only" },
        "OBX.5": {},
        "OBX.7": { "OBX.7.1": null },
      };

      const result = extractObxFields(obx);
      expect(result).to.deep.equal(["Name Only", "", "", ""]);
    });

    it("should handle completely missing obx fields gracefully", () => {
      const obx = {};
      const result = extractObxFields(obx);
      expect(result).to.deep.equal(["", "", "", ""]);
    });
  });

  describe("interpretResultStatus", () => {
    it("should return NORMAL for values within range", () => {
      expect(interpretResultStatus("45", "40-50")).to.equal("status: NORMAL");
    });

    it("should return HIGH for values above range", () => {
      expect(interpretResultStatus("60", "40-50")).to.equal("status: HIGH");
    });

    it("should return LOW for values below range", () => {
      expect(interpretResultStatus("30", "40-50")).to.equal("status: LOW");
    });

    it("should return UNDEFINED for missing value or range", () => {
      expect(interpretResultStatus("", "40-50")).to.equal("status: UNDEFINED");
      expect(interpretResultStatus("a", "40-50")).to.equal("status: UNDEFINED");
      expect(interpretResultStatus(null, "40-50")).to.equal(
        "status: UNDEFINED"
      );
      expect(interpretResultStatus(undefined, "40-50")).to.equal(
        "status: UNDEFINED"
      );

      expect(interpretResultStatus("45", "4050")).to.equal("status: UNDEFINED");
      expect(interpretResultStatus("45", "")).to.equal("status: UNDEFINED");
      expect(interpretResultStatus("45", "a")).to.equal("status: UNDEFINED");
      expect(interpretResultStatus("45", "40-")).to.equal("status: UNDEFINED");
      expect(interpretResultStatus("45", null)).to.equal("status: UNDEFINED");
      expect(interpretResultStatus("45", undefined)).to.equal(
        "status: UNDEFINED"
      );
    });
  });
});
