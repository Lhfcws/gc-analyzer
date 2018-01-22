testJson = {
  "GC Stats": {
    "Full GC Stats": {
      "GC avg interval": 0.0,
      "GC min time": 1164.98,
      "GC max time": 1164.98,
      "GC count": 1,
      "GC avg time": 1164.98,
      "GC total time": 1164.98
    },
    "Major GC Stats": {
      "GC avg interval": 0.0,
      "GC min time": 1164.98,
      "GC max time": 1164.98,
      "GC count": 1,
      "GC avg time": 1164.98,
      "GC total time": 1164.98
    },
    "Total GC Stats": {
      "GC avg interval": 6893.2,
      "GC min time": 19.12,
      "GC max time": 1164.98,
      "GC count": 11,
      "GC avg time": 140.51,
      "GC total time": 1545.67
    },
    "GC Duration": {
      "YoungGen": {
        "xdata": [1514536393130, 1514536393553, 1514536393981, 1514536394474, 1514536394991, 1514536395441, 1514536396009, 1514536396696, 1514536397231, 1514536432593],
        "mb": [256.140625, 254.982421875, 255.7060546875, 253.279296875, 267.1611328125, 269.6484375, 260.41015625, 254.9150390625, 255.4775390625, 236.62109375]
      },
      "OldGen": {
        "xdata": [1514536462062],
        "mb": [10.39453125]
      }
    },
    "Minor GC Stats": {
      "GC avg interval": 4384.77,
      "GC min time": 19.12,
      "GC max time": 91.0,
      "GC count": 10,
      "GC avg time": 38.06,
      "GC total time": 380.69
    },
    "Paused GC Stats": {
      "GC avg interval": 0.0,
      "GC min time": 1164.98,
      "GC max time": 1164.98,
      "GC count": 1,
      "GC avg time": 1164.98,
      "GC total time": 1164.98
    }
  },
  "JVM Heap": {
    "Heap": {
      "xdata": [1514536393130, 1514536393553, 1514536393981, 1514536394474, 1514536394991, 1514536395441, 1514536396009, 1514536396696, 1514536397231, 1514536432593, 1514536462062],
      "After GC": [278.193359375, 279.22265625, 279.5244140625, 282.2666015625, 282.0185546875, 274.734375, 270.33203125, 271.4296875, 272.095703125, 291.494140625, 252.990234375],
      "allocated": [3968.0, 3968.0, 3968.0, 3968.0, 3968.0, 3968.0, 3968.0, 3968.0, 3968.0, 3968.0, 3968.0],
      "Before GC": [534.2080078125, 534.193359375, 535.22265625, 535.5244140625, 538.2666015625, 538.0185546875, 530.734375, 526.33203125, 527.4296875, 528.095703125, 405.4091796875]
    },
    "OldGen": {
      "xdata": [1514536462062],
      "After GC": [252.990234375],
      "allocated": [3584.0],
      "Before GC": [263.384765625]
    },
    "YoungGen": {
      "xdata": [1514536393130, 1514536393553, 1514536393981, 1514536394474, 1514536394991, 1514536395441, 1514536396009, 1514536396696, 1514536397231, 1514536432593],
      "After GC": [32.3095703125, 33.3271484375, 33.62109375, 36.341796875, 25.1806640625, 11.5322265625, 7.1220703125, 8.20703125, 8.7294921875, 28.1083984375],
      "allocated": [384.0, 384.0, 384.0, 384.0, 384.0, 384.0, 384.0, 384.0, 384.0, 384.0],
      "Before GC": [288.4501953125, 288.3095703125, 289.3271484375, 289.62109375, 292.341796875, 281.1806640625, 267.5322265625, 263.1220703125, 264.20703125, 264.7294921875]
    },
    "_comments": "Default heap measure is MB, time measure is MS.",
    "summary": {
      "allocated": {
        "YoungGen": 384.0,
        "OldGen": 3584.0
      },
      "peak": {
        "YoungGen": 384.0,
        "OldGen": 3584.0
      }
    }
  },
  "_meta": {
    "gctype": "CMS"
  },
  "Summary": {
    "filename": "gc.parnew.log",
    "duration": 68932
  },
  "CMS Stats": {
    "CMS Total Pause Time": {
      "pause": 0.0,
      "concurrent": 0.0
    },
    "Avg Time": {
      "Concurrent Reset": 0.0,
      "Final Remark": 0.0,
      "Concurrent Abortable Preclean": 0.0,
      "Concurrent Mark": 0.0,
      "Initial Mark": 0.0,
      "Concurrent Sweep": 0.0,
      "Concurrent Preclean": 0.0,
      "Young GC": 38.06
    },
    "_comments": "Only Final Remark & Initial Mark will cause STW in CMS.",
    "CMS Avg Pause Time": {
      "pause": 0.0,
      "concurrent": 0.0
    },
    "Total Time": {
      "Concurrent Reset": 0.0,
      "Final Remark": 0.0,
      "Concurrent Abortable Preclean": 0.0,
      "Concurrent Mark": 0.0,
      "Initial Mark": 0.0,
      "Concurrent Sweep": 0.0,
      "Concurrent Preclean": 0.0,
      "Young GC": 380.69
    }
  }
};