var Entry = /** @class */ (function () {
    function Entry(name, strength, relationship) {
        this.name = name;
        this.strength = strength;
        this.relationship = relationship;
    }
    Entry.prototype.format = function () {
        return this.name + ": " + this.relationship + " (" + this.strength + ")";
    };
    return Entry;
}());
function validRelationship(rel) {
    return ["ally", "neutral", "enemy"].indexOf(rel) != -1;
}
function formatData(rawData) {
    var formatted = "";
    for (var i = 0; i < rawData.factions.length; i++) {
        var faction = rawData.factions[i];
        if (validRelationship(faction.relationship)) {
            var entry = new Entry(faction.name, faction.strength, faction.relationship);
            formatted += entry.format() + "\n";
        }
        else {
            throw new Error("Unknown relationship type: '" + faction.relationship + "'");
        }
    }
    return formatted;
}
