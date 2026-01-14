import { expect } from "chai";
import { extractComponents } from "../src/extractComponents.js";

describe("extractComponents()", () => {
  it("should join all 5 subfields with ^", () => {
    const msgField = {
      "PV1.17.1": "John",
      "PV1.17.2": "A",
      "PV1.17.3": "Smith",
      "PV1.17.4": "MD",
      "PV1.17.5": "PhD",
    };

    const result = extractComponents(msgField, "PV1.17");
    expect(result).to.equal("John^A^Smith^MD^PhD");
  });

  it("should remove empty subfields", () => {
    const msgField = {
      "PV1.17.1": "John",
      "PV1.17.2": "",
      "PV1.17.3": "Smith",
    };

    const result = extractComponents(msgField, "PV1.17");
    expect(result).to.equal("John^Smith");
  });

  it("should handle missing subfields gracefully", () => {
    const msgField = {
      "PV1.17.1": "John",
      // others missing
    };

    const result = extractComponents(msgField, "PV1.17");
    expect(result).to.equal("John");
  });

  it("should return empty string if field object is empty", () => {
    const msgField = {};
    const result = extractComponents(msgField, "PV1.17");
    expect(result).to.equal("");
  });

  it("should return empty string if field object is null", () => {
    const result = extractComponents(null, "PV1.17");
    expect(result).to.equal("");
  });

  it("should return empty string if field object is undefined", () => {
    const result = extractComponents(undefined, "PV1.17");
    expect(result).to.equal("");
  });

  it("should only consider first 5 subfields even if more exist", () => {
    const msgField = {
      "PV1.17.1": "A",
      "PV1.17.2": "B",
      "PV1.17.3": "C",
      "PV1.17.4": "D",
      "PV1.17.5": "E",
      "PV1.17.6": "F", // should be ignored
    };

    const result = extractComponents(msgField, "PV1.17");
    expect(result).to.equal("A^B^C^D^E");
  });

  it("should support fallback logic (PV1 then ORC)", () => {
    const pv1Field = {}; // empty PV1.17
    const orcField = { "ORC.12.1": "Fallback" }; // ORC.12 has value

    let physician = extractComponents(pv1Field, "PV1.17");
    if (!physician) {
      physician = extractComponents(orcField, "ORC.12");
    }

    expect(physician).to.equal("Fallback");
  });
});
