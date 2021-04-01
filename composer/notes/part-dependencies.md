# Part dependencies

Context: I'm trying to figure out how to structure different parts, and which parts depend on others.
In this case, dependency especially means that if one part changes, then any dependent parts need 
to be re-calculated or even regenerated.

Parts that I can think of right now:
- Time Signature
- Tempo
- Key signature
- Sections (like verse, chorus, etc.)
- Chord progression
- Melody
- Measures(?)

Dependencies I can think of so far:


- Melody depends on Chord progression and Time Signature
- Chord progression depends on sections
- If I include the concept of duration to chords in a chord progression, then chord progression
probably becomes dependent on time signature
