global.logger = { warn: () => {}, info: () => {}, error: () => {} };

import { expect } from "chai";
import { getMessageType, countSegments } from "../src/metadataUtils.js";

describe("Metadata Utilities", () => {
  // ---------------------------------------------------------------------
  // getMessageType()
  // ---------------------------------------------------------------------

  it("should return correct message type when MSH.9.1 and MSH.9.2 exist", () => {
    const msg = {
      MSH: {
        "MSH.9": {
          "MSH.9.1": "ORU",
          "MSH.9.2": "R01",
        },
      },
    };

    expect(getMessageType(msg)).to.equal("ORU^R01");
  });

  it("should return empty string if msg is null", () => {
    expect(getMessageType(null)).to.equal("");
  });

  it("should return empty string if MSH is missing", () => {
    expect(getMessageType({})).to.equal("");
  });

  it("should return empty string if MSH.9 is missing", () => {
    const msg = { MSH: {} };
    expect(getMessageType(msg)).to.equal("");
  });

  it("should handle missing subfields gracefully", () => {
    const msg = {
      MSH: {
        "MSH.9": {
          "MSH.9.1": "",
          "MSH.9.2": "",
        },
      },
    };

    expect(getMessageType(msg)).to.equal("^");
  });

  // ---------------------------------------------------------------------
  // countSegments()
  // ---------------------------------------------------------------------

  it("should count segments correctly for repeating OBX", () => {
    const msg = {
      OBX: [{ "OBX.1": "1" }, { "OBX.1": "2" }, { "OBX.1": "3" }],
    };

    expect(countSegments(msg, "OBX")).to.equal(3);
  });

  it("should return 0 when segment does not exist", () => {
    expect(countSegments({}, "NTE")).to.equal(0);
  });

  it("should return 0 when msg is null", () => {
    expect(countSegments(null, "OBX")).to.equal(0);
  });

  it("should work even if segment is an empty array", () => {
    const msg = { OBX: [] };
    expect(countSegments(msg, "OBX")).to.equal(0);
  });

  it("should work if Mirth creates segment object instead of array", () => {
    // Sometimes Mirth creates { '0': {...}, '1': {...} } instead of an array
    const msg = {
      OBX: {
        0: { "OBX.1": "A" },
        1: { "OBX.1": "B" },
      },
    };

    // countSegments uses Object.keys so this works properly
    expect(countSegments(msg, "OBX")).to.equal(2);
  });
});
