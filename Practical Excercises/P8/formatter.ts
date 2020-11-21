type RelType = "ally" | "neutral" | "enemy";

class Entry {
    name: string;
    strength: number;
    relationship: RelType;

    constructor(name: string, strength: number, relationship: RelType) {
        this.name = name;
        this.strength = strength;
        this.relationship = relationship;
    }

    format(): string {
        return `${this.name}: ${this.relationship} (${this.strength})`;
    }
}

function validRelationship(rel: any): rel is RelType {
    return ["ally", "neutral", "enemy"].indexOf(rel) != -1;
}

function formatData(rawData: any): string {
    let formatted = "";
    for (let i = 0; i < rawData.factions.length; i++) {
        let faction = rawData.factions[i];

        if (validRelationship(faction.relationship)) {
            let entry: Entry = new Entry(
                faction.name,
                faction.strength,
                faction.relationship
            );
            formatted += entry.format() + "\n";
        }
        else {
            throw new Error(
                "Unknown relationship type: '" + faction.relationship + "'"
            );
        }
    }
    return formatted;
}
