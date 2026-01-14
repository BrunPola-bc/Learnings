import { expect } from "chai";
import { capitalize, normalizeName } from "../src/nameUtils.js";

describe("Name Utilities", () => {
  describe("capitalize()", () => {
    it("should capitalize first letter and lowercase the rest", () => {
      expect(capitalize("john")).to.equal("John");
      expect(capitalize("DOE")).to.equal("Doe");
      expect(capitalize("M")).to.equal("M");
      expect(capitalize("m")).to.equal("M");
      expect(capitalize("")).to.equal("");
      expect(capitalize(null)).to.equal(null);
    });
  });

  describe("normalizeName()", () => {
    it("should capitalize all name fields of js object", () => {
      const field = {
        "PID.5.1": "joHN",
        "PID.5.2": "doE",
        "PID.5.3": "mARy",
      };
      normalizeName("PID.5", field);
      expect(field["PID.5.1"]).to.equal("John");
      expect(field["PID.5.2"]).to.equal("Doe");
      expect(field["PID.5.3"]).to.equal("Mary");
    });

    it("should handle missing fields gracefully", () => {
      const field = {};
      expect(() => normalizeName("PID.5", field)).to.not.throw();
    });
  });
});
