#!/usr/bin/env python3
"""
Auto-review repo candidates bằng LLM.

Entry point — delegates to review/ module.

Usage:
  set LLM_API_KEY=your-key
  python scripts/auto-review-candidates.py [--dry-run] [--admin-token TOKEN]
"""
import sys
import os

_SELF_DIR = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, _SELF_DIR)

from review.__main__ import main

if __name__ == "__main__":
    main()
