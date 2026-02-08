export const mockObservations = [
  {
    id: 1,
    name: "Jupiter and its Galilean Moons",
    description:
      "Observation of Jupiter showing all four Galilean moons: Io, Europa, Ganymede, and Callisto. Jupiter's Great Red Spot was clearly visible.",
    observationTime: "2024-01-15T21:30:00",
    author: {
      id: 1,
      firstName: "Galileo",
      lastName: "Galilei",
    },
    celestialObjects: ["Jupiter", "Io", "Europa", "Ganymede", "Callisto"],
  },
  {
    id: 2,
    name: "Andromeda Galaxy Deep Sky",
    description:
      "Extended exposure observation of the Andromeda Galaxy (M31) revealing spiral structure and companion galaxies M32 and M110.",
    observationTime: "2024-01-20T23:45:00",
    author: {
      id: 2,
      firstName: "Edwin",
      lastName: "Hubble",
    },
    celestialObjects: ["Andromeda Galaxy", "M31", "M32", "M110"],
  },
  {
    id: 3,
    name: "Saturn's Rings at Opposition",
    description:
      "Saturn observation at opposition showing ring system at maximum tilt. Cassini Division clearly visible. Titan also observed.",
    observationTime: "2024-02-10T22:15:00",
    author: {
      id: 3,
      firstName: "Carl",
      lastName: "Sagan",
    },
    celestialObjects: ["Saturn", "Titan", "Saturn's Rings"],
  },
  {
    id: 4,
    name: "Orion Nebula Complex",
    description:
      "Observation of the Orion Nebula (M42) and surrounding nebulosity. The Trapezium cluster resolved into individual stars.",
    observationTime: "2024-02-14T20:00:00",
    author: {
      id: 1,
      firstName: "Galileo",
      lastName: "Galilei",
    },
    celestialObjects: ["Orion Nebula", "M42", "Trapezium Cluster"],
  },
];
