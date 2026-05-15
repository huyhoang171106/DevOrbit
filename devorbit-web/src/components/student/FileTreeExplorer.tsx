import { useState, useMemo } from 'react'
import { CaretRight, Folder, FolderOpen, FileCode, FileImage, FileText, FileZip, FilePy, FileTsx, FileJs, FileCss, FileHtml, FileMd, FileSql, FileVideo, FileAudio, FilePdf, FileIni, FileLock } from '@phosphor-icons/react'
import { motion, AnimatePresence } from 'framer-motion'
import type { RepoTreeItem } from '../../types/api'

type TreeNode = {
  name: string
  path: string
  type: 'blob' | 'tree'
  size: number | null
  children: TreeNode[]
  expanded: boolean
}

function buildTree(items: RepoTreeItem[]): TreeNode[] {
  const root: TreeNode[] = []
  const map = new Map<string, TreeNode>()

  const sorted = [...items].sort((a, b) => {
    if (a.type !== b.type) return a.type === 'tree' ? -1 : 1
    return a.path.localeCompare(b.path)
  })

  for (const item of sorted) {
    const node: TreeNode = {
      name: item.name,
      path: item.path,
      type: item.type,
      size: item.size,
      children: [],
      expanded: item.type === 'tree' && item.path.split('/').length <= 2,
    }
    map.set(item.path, node)

    const slashIdx = item.path.lastIndexOf('/')
    if (slashIdx === -1) {
      root.push(node)
    } else {
      const parentPath = item.path.slice(0, slashIdx)
      const parent = map.get(parentPath)
      if (parent) parent.children.push(node)
      else root.push(node)
    }
  }

  return root
}

function fileIcon(name: string) {
  const ext = name.includes('.') ? name.split('.').pop()!.toLowerCase() : ''
  if (!ext) return FileLock
  const iconMap: Record<string, typeof FileCode> = {
    ts: FileTsx, tsx: FileTsx, js: FileJs, jsx: FileJs,
    py: FilePy, rs: FileCode, go: FileCode, java: FileCode, kt: FileCode, scala: FileCode,
    css: FileCss, scss: FileCss, less: FileCss,
    html: FileHtml, htm: FileHtml,
    md: FileMd, mdx: FileMd,
    json: FileIni, yaml: FileIni, yml: FileIni, toml: FileIni,
    sql: FileSql,
    png: FileImage, jpg: FileImage, jpeg: FileImage, gif: FileImage, svg: FileImage, webp: FileImage, ico: FileImage,
    mp4: FileVideo, webm: FileVideo, avi: FileVideo,
    mp3: FileAudio, wav: FileAudio, ogg: FileAudio,
    pdf: FilePdf,
    zip: FileZip, tar: FileZip, gz: FileZip, rar: FileZip, '7z': FileZip,
    gitignore: FileLock, gitkeep: FileLock,
    env: FileLock, editorconfig: FileIni, prettierrc: FileIni, eslintrc: FileIni,
    node_modules: FileLock,
  }
  return iconMap[ext] ?? (ext.length <= 4 ? FileCode : FileText)
}

function formatSize(bytes: number | null): string {
  if (bytes === null) return ''
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
}

function TreeNodeRow({ node, depth }: { node: TreeNode; depth: number }) {
  const [expanded, setExpanded] = useState(node.expanded)
  const isDir = node.type === 'tree'
  const Icon = isDir ? (expanded ? FolderOpen : Folder) : fileIcon(node.name)
  const hasChildren = isDir && node.children.length > 0

  return (
    <div>
      <button
        onClick={() => isDir && setExpanded(!expanded)}
        className={`w-full flex items-center gap-2 px-2 py-1.5 rounded-lg transition-colors text-left
          hover:bg-orbit-accent/5 text-[13px] group
          ${depth === 0 ? 'font-semibold text-orbit-text' : 'text-orbit-text-secondary'}`}
      >
        {isDir && (
          <motion.div
            animate={{ rotate: expanded ? 90 : 0 }}
            transition={{ duration: 0.15 }}
            className="text-orbit-text-muted shrink-0"
          >
            <CaretRight size={12} weight="bold" />
          </motion.div>
        )}
        {!isDir && <span className="w-3 shrink-0" />}
        <Icon
          size={16}
          weight={isDir ? 'fill' : 'regular'}
          className={`shrink-0 ${isDir ? 'text-orbit-accent/70' : 'text-orbit-text-muted'}`}
        />
        <span className="truncate flex-1">{node.name}</span>
        {!isDir && node.size !== null && (
          <span className="text-[11px] text-orbit-text-muted tabular-nums opacity-0 group-hover:opacity-100 transition-opacity">
            {formatSize(node.size)}
          </span>
        )}
      </button>
      <AnimatePresence initial={false}>
        {isDir && expanded && hasChildren && (
          <motion.div
            key={node.path}
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: 'auto', opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.15 }}
            className="overflow-hidden"
          >
            {node.children.map((child) => (
              <TreeNodeRow key={child.path} node={child} depth={depth + 1} />
            ))}
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  )
}

export function FileTreeExplorer({ items, loading }: { items: RepoTreeItem[]; loading?: boolean }) {
  const tree = useMemo(() => buildTree(items), [items])

  if (loading) {
    return (
      <div className="orbit-card-glow p-6">
        <div className="flex items-center gap-3 mb-4">
          <div className="h-5 w-5 rounded bg-orbit-elevated animate-pulse" />
          <div className="h-4 w-32 rounded bg-orbit-elevated animate-pulse" />
        </div>
        {Array.from({ length: 5 }).map((_, i) => (
          <div key={i} className="flex items-center gap-2 px-2 py-2">
            <div className="h-3 w-3 rounded bg-orbit-elevated animate-pulse" />
            <div className={`h-3 rounded bg-orbit-elevated animate-pulse`} style={{ width: `${60 + Math.random() * 30}%` }} />
          </div>
        ))}
      </div>
    )
  }

  if (items.length === 0) return null

  const fileCount = items.filter((i) => i.type === 'blob').length
  const dirCount = items.filter((i) => i.type === 'tree').length

  return (
    <div className="orbit-card-glow p-6">
      <div className="flex items-center gap-3 mb-4">
        <FolderOpen size={20} weight="fill" className="text-orbit-accent/70" />
        <h3 className="heading-5 text-orbit-text">Cấu trúc thư mục</h3>
        <div className="flex items-center gap-2 ml-auto">
          <span className="text-[11px] text-orbit-text-muted tabular-nums">{dirCount} thư mục</span>
          <span className="text-orbit-text-muted">·</span>
          <span className="text-[11px] text-orbit-text-muted tabular-nums">{fileCount} tệp</span>
        </div>
      </div>
      <div className="max-h-[500px] overflow-y-auto custom-scrollbar">
        {tree.map((node) => (
          <TreeNodeRow key={node.path} node={node} depth={0} />
        ))}
      </div>
    </div>
  )
}
