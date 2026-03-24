"use client"

import * as React from "react"
import { format, setMonth, setYear } from "date-fns"
import { zhTW } from "date-fns/locale"
import {
    CalendarIcon,
    ChevronLeft,
    ChevronRight,
    ChevronLeftIcon,
    ChevronRightIcon,
    ChevronDownIcon,
    ChevronUpIcon,
    CheckIcon,
} from "lucide-react"
import { Slot } from "@radix-ui/react-slot"
import { cva, type VariantProps } from "class-variance-authority"
import * as PopoverPrimitive from "@radix-ui/react-popover"
import * as SelectPrimitive from "@radix-ui/react-select"
import { DayButton, DayPicker, getDefaultClassNames } from "react-day-picker"
import { DateRange } from "react-day-picker"

import { cn } from "@/components/lib/utils"

// ============================================
// Button 組件
// ============================================
const buttonVariants = cva(
    "inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-all disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg:not([class*='size-'])]:size-4 shrink-0 [&_svg]:shrink-0 outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive",
    {
        variants: {
            variant: {
                default: "bg-primary text-primary-foreground hover:bg-primary/90",
                destructive:
                    "bg-destructive text-white hover:bg-destructive/90 focus-visible:ring-destructive/20 dark:focus-visible:ring-destructive/40 dark:bg-destructive/60",
                outline:
                    "border bg-background shadow-xs hover:bg-accent hover:text-accent-foreground dark:bg-input/30 dark:border-input dark:hover:bg-input/50",
                secondary:
                    "bg-secondary text-secondary-foreground hover:bg-secondary/80",
                ghost:
                    "hover:bg-accent hover:text-accent-foreground dark:hover:bg-accent/50",
                link: "text-primary underline-offset-4 hover:underline",
            },
            size: {
                default: "h-9 px-4 py-2 has-[>svg]:px-3",
                sm: "h-8 rounded-md gap-1.5 px-3 has-[>svg]:px-2.5",
                lg: "h-10 rounded-md px-6 has-[>svg]:px-4",
                icon: "size-9",
                "icon-sm": "size-8",
                "icon-lg": "size-10",
            },
        },
        defaultVariants: {
            variant: "default",
            size: "default",
        },
    }
)

function Button({
                    className,
                    variant,
                    size,
                    asChild = false,
                    ...props
                }: React.ComponentProps<"button"> &
    VariantProps<typeof buttonVariants> & {
    asChild?: boolean
}) {
    const Comp = asChild ? Slot : "button"

    return (
        <Comp
            data-slot="button"
            className={cn(buttonVariants({ variant, size, className }))}
            {...props}
        />
    )
}

// ============================================
// Popover 組件
// ============================================
function Popover({
                     ...props
                 }: React.ComponentProps<typeof PopoverPrimitive.Root>) {
    return <PopoverPrimitive.Root data-slot="popover" {...props} />
}

function PopoverTrigger({
                            ...props
                        }: React.ComponentProps<typeof PopoverPrimitive.Trigger>) {
    return <PopoverPrimitive.Trigger data-slot="popover-trigger" {...props} />
}

function PopoverContent({
                            className,
                            align = "center",
                            sideOffset = 4,
                            ...props
                        }: React.ComponentProps<typeof PopoverPrimitive.Content>) {
    return (
        <PopoverPrimitive.Portal>
            <PopoverPrimitive.Content
                data-slot="popover-content"
                align={align}
                sideOffset={sideOffset}
                className={cn(
                    "bg-popover text-popover-foreground data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0 data-[state=closed]:zoom-out-95 data-[state=open]:zoom-in-95 data-[side=bottom]:slide-in-from-top-2 data-[side=left]:slide-in-from-right-2 data-[side=right]:slide-in-from-left-2 data-[side=top]:slide-in-from-bottom-2 z-50 w-72 origin-(--radix-popover-content-transform-origin) rounded-md border p-4 shadow-md outline-hidden",
                    className
                )}
                {...props}
            />
        </PopoverPrimitive.Portal>
    )
}

// ============================================
// Select 組件
// ============================================
function Select({
                    ...props
                }: React.ComponentProps<typeof SelectPrimitive.Root>) {
    return <SelectPrimitive.Root data-slot="select" {...props} />
}

function SelectValue({
                         ...props
                     }: React.ComponentProps<typeof SelectPrimitive.Value>) {
    return <SelectPrimitive.Value data-slot="select-value" {...props} />
}

function SelectTrigger({
                           className,
                           size = "default",
                           children,
                           ...props
                       }: React.ComponentProps<typeof SelectPrimitive.Trigger> & {
    size?: "sm" | "default"
}) {
    return (
        <SelectPrimitive.Trigger
            data-slot="select-trigger"
            data-size={size}
            className={cn(
                "border-input data-[placeholder]:text-muted-foreground [&_svg:not([class*='text-'])]:text-muted-foreground focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive dark:bg-input/30 dark:hover:bg-input/50 flex w-fit items-center justify-between gap-2 rounded-md border bg-transparent px-3 py-2 text-sm whitespace-nowrap shadow-xs transition-[color,box-shadow] outline-none focus-visible:ring-[3px] disabled:cursor-not-allowed disabled:opacity-50 data-[size=default]:h-9 data-[size=sm]:h-8 *:data-[slot=select-value]:line-clamp-1 *:data-[slot=select-value]:flex *:data-[slot=select-value]:items-center *:data-[slot=select-value]:gap-2 [&_svg]:pointer-events-none [&_svg]:shrink-0 [&_svg:not([class*='size-'])]:size-4",
                className
            )}
            {...props}
        >
            {children}
            <SelectPrimitive.Icon asChild>
                <ChevronDownIcon className="size-4 opacity-50" />
            </SelectPrimitive.Icon>
        </SelectPrimitive.Trigger>
    )
}

function SelectContent({
                           className,
                           children,
                           position = "popper",
                           ...props
                       }: React.ComponentProps<typeof SelectPrimitive.Content>) {
    return (
        <SelectPrimitive.Portal>
            <SelectPrimitive.Content
                data-slot="select-content"
                className={cn(
                    "bg-popover text-popover-foreground data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0 data-[state=closed]:zoom-out-95 data-[state=open]:zoom-in-95 data-[side=bottom]:slide-in-from-top-2 data-[side=left]:slide-in-from-right-2 data-[side=right]:slide-in-from-left-2 data-[side=top]:slide-in-from-bottom-2 relative z-50 max-h-(--radix-select-content-available-height) min-w-[8rem] origin-(--radix-select-content-transform-origin) overflow-x-hidden overflow-y-auto rounded-md border shadow-md",
                    position === "popper" &&
                    "data-[side=bottom]:translate-y-1 data-[side=left]:-translate-x-1 data-[side=right]:translate-x-1 data-[side=top]:-translate-y-1",
                    className
                )}
                position={position}
                {...props}
            >
                <SelectScrollUpButton />
                <SelectPrimitive.Viewport
                    className={cn(
                        "p-1",
                        position === "popper" &&
                        "h-[var(--radix-select-trigger-height)] w-full min-w-[var(--radix-select-trigger-width)] scroll-my-1"
                    )}
                >
                    {children}
                </SelectPrimitive.Viewport>
                <SelectScrollDownButton />
            </SelectPrimitive.Content>
        </SelectPrimitive.Portal>
    )
}

function SelectItem({
                        className,
                        children,
                        ...props
                    }: React.ComponentProps<typeof SelectPrimitive.Item>) {
    return (
        <SelectPrimitive.Item
            data-slot="select-item"
            className={cn(
                "focus:bg-accent focus:text-accent-foreground [&_svg:not([class*='text-'])]:text-muted-foreground relative flex w-full cursor-default items-center gap-2 rounded-sm py-1.5 pr-8 pl-2 text-sm outline-hidden select-none data-[disabled]:pointer-events-none data-[disabled]:opacity-50 [&_svg]:pointer-events-none [&_svg]:shrink-0 [&_svg:not([class*='size-'])]:size-4 *:[span]:last:flex *:[span]:last:items-center *:[span]:last:gap-2",
                className
            )}
            {...props}
        >
      <span className="absolute right-2 flex size-3.5 items-center justify-center">
        <SelectPrimitive.ItemIndicator>
          <CheckIcon className="size-4" />
        </SelectPrimitive.ItemIndicator>
      </span>
            <SelectPrimitive.ItemText>{children}</SelectPrimitive.ItemText>
        </SelectPrimitive.Item>
    )
}

function SelectScrollUpButton({
                                  className,
                                  ...props
                              }: React.ComponentProps<typeof SelectPrimitive.ScrollUpButton>) {
    return (
        <SelectPrimitive.ScrollUpButton
            data-slot="select-scroll-up-button"
            className={cn(
                "flex cursor-default items-center justify-center py-1",
                className
            )}
            {...props}
        >
            <ChevronUpIcon className="size-4" />
        </SelectPrimitive.ScrollUpButton>
    )
}

function SelectScrollDownButton({
                                    className,
                                    ...props
                                }: React.ComponentProps<typeof SelectPrimitive.ScrollDownButton>) {
    return (
        <SelectPrimitive.ScrollDownButton
            data-slot="select-scroll-down-button"
            className={cn(
                "flex cursor-default items-center justify-center py-1",
                className
            )}
            {...props}
        >
            <ChevronDownIcon className="size-4" />
        </SelectPrimitive.ScrollDownButton>
    )
}

// ============================================
// Calendar 組件
// ============================================
function Calendar({
                      className,
                      classNames,
                      showOutsideDays = true,
                      captionLayout = "label",
                      buttonVariant = "ghost",
                      formatters,
                      components,
                      ...props
                  }: React.ComponentProps<typeof DayPicker> & {
    buttonVariant?: React.ComponentProps<typeof Button>["variant"]
}) {
    const defaultClassNames = getDefaultClassNames()

    return (
        <DayPicker
            showOutsideDays={showOutsideDays}
            className={cn(
                "bg-background group/calendar p-3 [--cell-size:--spacing(8)] [[data-slot=card-content]_&]:bg-transparent [[data-slot=popover-content]_&]:bg-transparent",
                String.raw`rtl:**:[.rdp-button\_next>svg]:rotate-180`,
                String.raw`rtl:**:[.rdp-button\_previous>svg]:rotate-180`,
                className
            )}
            captionLayout={captionLayout}
            formatters={{
                formatMonthDropdown: (date) =>
                    date.toLocaleString("default", { month: "short" }),
                ...formatters,
            }}
            classNames={{
                root: cn("w-fit", defaultClassNames.root),
                months: cn(
                    "flex gap-4 flex-col md:flex-row relative",
                    defaultClassNames.months
                ),
                month: cn("flex flex-col w-full gap-4", defaultClassNames.month),
                nav: cn(
                    "flex items-center gap-1 w-full absolute top-0 inset-x-0 justify-between",
                    defaultClassNames.nav
                ),
                button_previous: cn(
                    buttonVariants({ variant: buttonVariant }),
                    "size-(--cell-size) aria-disabled:opacity-50 p-0 select-none",
                    defaultClassNames.button_previous
                ),
                button_next: cn(
                    buttonVariants({ variant: buttonVariant }),
                    "size-(--cell-size) aria-disabled:opacity-50 p-0 select-none",
                    defaultClassNames.button_next
                ),
                month_caption: cn(
                    "flex items-center justify-center h-(--cell-size) w-full px-(--cell-size)",
                    defaultClassNames.month_caption
                ),
                dropdowns: cn(
                    "w-full flex items-center text-sm font-medium justify-center h-(--cell-size) gap-1.5",
                    defaultClassNames.dropdowns
                ),
                dropdown_root: cn(
                    "relative has-focus:border-ring border border-input shadow-xs has-focus:ring-ring/50 has-focus:ring-[3px] rounded-md",
                    defaultClassNames.dropdown_root
                ),
                dropdown: cn(
                    "absolute bg-popover inset-0 opacity-0",
                    defaultClassNames.dropdown
                ),
                caption_label: cn(
                    "select-none font-medium",
                    captionLayout === "label"
                        ? "text-sm"
                        : "rounded-md pl-2 pr-1 flex items-center gap-1 text-sm h-8 [&>svg]:text-muted-foreground [&>svg]:size-3.5",
                    defaultClassNames.caption_label
                ),
                table: "w-full border-collapse",
                weekdays: cn("flex", defaultClassNames.weekdays),
                weekday: cn(
                    "text-muted-foreground rounded-md flex-1 font-normal text-[0.8rem] select-none",
                    defaultClassNames.weekday
                ),
                week: cn("flex w-full mt-2", defaultClassNames.week),
                week_number_header: cn(
                    "select-none w-(--cell-size)",
                    defaultClassNames.week_number_header
                ),
                week_number: cn(
                    "text-[0.8rem] select-none text-muted-foreground",
                    defaultClassNames.week_number
                ),
                day: cn(
                    "relative w-full h-full p-0 text-center [&:first-child[data-selected=true]_button]:rounded-l-md [&:last-child[data-selected=true]_button]:rounded-r-md group/day aspect-square select-none",
                    defaultClassNames.day
                ),
                range_start: cn(
                    "rounded-l-md bg-accent",
                    defaultClassNames.range_start
                ),
                range_middle: cn("rounded-none", defaultClassNames.range_middle),
                range_end: cn("rounded-r-md bg-accent", defaultClassNames.range_end),
                today: cn(
                    "bg-accent text-accent-foreground rounded-md data-[selected=true]:rounded-none",
                    defaultClassNames.today
                ),
                outside: cn(
                    "text-muted-foreground aria-selected:text-muted-foreground",
                    defaultClassNames.outside
                ),
                disabled: cn(
                    "text-muted-foreground opacity-50",
                    defaultClassNames.disabled
                ),
                hidden: cn("invisible", defaultClassNames.hidden),
                ...classNames,
            }}
            components={{
                Root: ({ className, rootRef, ...props }) => {
                    return (
                        <div
                            data-slot="calendar"
                            ref={rootRef}
                            className={cn(className)}
                            {...props}
                        />
                    )
                },
                Chevron: ({ className, orientation, ...props }) => {
                    if (orientation === "left") {
                        return (
                            <ChevronLeftIcon className={cn("size-4", className)} {...props} />
                        )
                    }

                    if (orientation === "right") {
                        return (
                            <ChevronRightIcon
                                className={cn("size-4", className)}
                                {...props}
                            />
                        )
                    }

                    return (
                        <ChevronDownIcon className={cn("size-4", className)} {...props} />
                    )
                },
                DayButton: CalendarDayButton,
                WeekNumber: ({ children, ...props }) => {
                    return (
                        <td {...props}>
                            <div className="flex size-(--cell-size) items-center justify-center text-center">
                                {children}
                            </div>
                        </td>
                    )
                },
                ...components,
            }}
            {...props}
        />
    )
}

function CalendarDayButton({
                               className,
                               day,
                               modifiers,
                               ...props
                           }: React.ComponentProps<typeof DayButton>) {
    const defaultClassNames = getDefaultClassNames()

    const ref = React.useRef<HTMLButtonElement>(null)
    React.useEffect(() => {
        if (modifiers.focused) ref.current?.focus()
    }, [modifiers.focused])

    return (
        <Button
            ref={ref}
            variant="ghost"
            size="icon"
            data-day={day.date.toLocaleDateString()}
            data-selected-single={
                modifiers.selected &&
                !modifiers.range_start &&
                !modifiers.range_end &&
                !modifiers.range_middle
            }
            data-range-start={modifiers.range_start}
            data-range-end={modifiers.range_end}
            data-range-middle={modifiers.range_middle}
            className={cn(
                "data-[selected-single=true]:bg-primary data-[selected-single=true]:text-primary-foreground data-[range-middle=true]:bg-accent data-[range-middle=true]:text-accent-foreground data-[range-start=true]:bg-primary data-[range-start=true]:text-primary-foreground data-[range-end=true]:bg-primary data-[range-end=true]:text-primary-foreground group-data-[focused=true]/day:border-ring group-data-[focused=true]/day:ring-ring/50 dark:hover:text-accent-foreground flex aspect-square size-auto w-full min-w-(--cell-size) flex-col gap-1 leading-none font-normal group-data-[focused=true]/day:relative group-data-[focused=true]/day:z-10 group-data-[focused=true]/day:ring-[3px] data-[range-end=true]:rounded-md data-[range-end=true]:rounded-r-md data-[range-middle=true]:rounded-none data-[range-start=true]:rounded-md data-[range-start=true]:rounded-l-md [&>span]:text-xs [&>span]:opacity-70",
                defaultClassNames.day,
                className
            )}
            {...props}
        />
    )
}

// ============================================
// DatePicker 內部組件
// ============================================
const MONTHS = [
    "一月", "二月", "三月", "四月", "五月", "六月",
    "七月", "八月", "九月", "十月", "十一月", "十二月"
]

const currentYear = new Date().getFullYear()
const YEARS = Array.from({ length: 201 }, (_, i) => currentYear - 100 + i)

interface YearMonthSelectorProps {
    currentMonth: Date
    onMonthChange: (date: Date) => void
}

function YearMonthSelector({ currentMonth, onMonthChange }: YearMonthSelectorProps) {
    const [showSelector, setShowSelector] = React.useState(false)
    const selectedYear = currentMonth.getFullYear()
    const selectedMonth = currentMonth.getMonth()

    const handleYearChange = (year: string) => {
        const newDate = setYear(currentMonth, parseInt(year))
        onMonthChange(newDate)
    }

    const handleMonthChange = (month: string) => {
        const newDate = setMonth(currentMonth, parseInt(month))
        onMonthChange(newDate)
    }

    const goToPreviousMonth = () => {
        const newDate = new Date(currentMonth)
        newDate.setMonth(newDate.getMonth() - 1)
        onMonthChange(newDate)
    }

    const goToNextMonth = () => {
        const newDate = new Date(currentMonth)
        newDate.setMonth(newDate.getMonth() + 1)
        onMonthChange(newDate)
    }

    if (showSelector) {
        return (
            <div className="flex items-center justify-between px-1 py-2">
                <div className="flex items-center gap-2">
                    <Select value={selectedYear.toString()} onValueChange={handleYearChange}>
                        <SelectTrigger className="h-8 w-[90px] text-slate-900 border-slate-200">
                            <SelectValue />
                        </SelectTrigger>
                        <SelectContent
                            position="popper"
                            className="max-h-[200px] overflow-y-auto"
                            sideOffset={4}
                        >
                            {YEARS.map((year) => (
                                <SelectItem key={year} value={year.toString()}>
                                    {year}年
                                </SelectItem>
                            ))}
                        </SelectContent>
                    </Select>
                    <Select value={selectedMonth.toString()} onValueChange={handleMonthChange}>
                        <SelectTrigger className="h-8 w-[90px] text-slate-900 border-slate-200">
                            <SelectValue />
                        </SelectTrigger>
                        <SelectContent
                            position="popper"
                            sideOffset={4}
                        >
                            {MONTHS.map((month, index) => (
                                <SelectItem key={index} value={index.toString()}>
                                    {month}
                                </SelectItem>
                            ))}
                        </SelectContent>
                    </Select>
                </div>
                <Button
                    variant="ghost"
                    size="sm"
                    className="h-8 text-xs"
                    onClick={() => setShowSelector(false)}
                >
                    完成
                </Button>
            </div>
        )
    }

    return (
        <div className="flex items-center justify-between px-1 py-2">
            <Button
                variant="ghost"
                size="icon"
                className="h-7 w-7"
                onClick={goToPreviousMonth}
            >
                <ChevronLeft className="h-4 w-4" />
            </Button>
            <button
                type="button"
                className="text-sm font-medium hover:bg-accent hover:text-accent-foreground rounded-md px-2 py-1 transition-colors cursor-pointer"
                onClick={() => setShowSelector(true)}
            >
                {format(currentMonth, "yyyy年 MM月", { locale: zhTW })}
            </button>
            <Button
                variant="ghost"
                size="icon"
                className="h-7 w-7"
                onClick={goToNextMonth}
            >
                <ChevronRight className="h-4 w-4" />
            </Button>
        </div>
    )
}

interface CalendarFooterProps {
    onClear: () => void
    onToday: () => void
}

function CalendarFooter({ onClear, onToday }: CalendarFooterProps) {
    return (
        <div className="flex items-center justify-between border-t px-3 py-2">
            <Button
                variant="ghost"
                size="sm"
                className="h-8 text-muted-foreground hover:text-foreground"
                onClick={onClear}
            >
                清除
            </Button>
            <Button
                variant="ghost"
                size="sm"
                className="h-8"
                onClick={onToday}
            >
                今天
            </Button>
        </div>
    )
}

// ============================================
// 匯出的 DatePicker 組件
// ============================================
interface DatePickerProps {
    date?: Date
    onDateChange?: (date: Date | undefined) => void
    placeholder?: string
    className?: string
    disabled?: boolean
}

export function DatePicker({
                               date,
                               onDateChange,
                               placeholder = "選擇日期",
                               className,
                               disabled = false,
                           }: DatePickerProps) {
    const [selectedDate, setSelectedDate] = React.useState<Date | undefined>(date)
    const [currentMonth, setCurrentMonth] = React.useState<Date>(date || new Date())
    const [open, setOpen] = React.useState(false)

    const handleSelect = (newDate: Date | undefined) => {
        setSelectedDate(newDate)
        onDateChange?.(newDate)
        if (newDate) {
            setOpen(false)
        }
    }

    const handleClear = () => {
        setSelectedDate(undefined)
        onDateChange?.(undefined)
        setCurrentMonth(new Date())
    }

    const handleToday = () => {
        const today = new Date()
        setSelectedDate(today)
        setCurrentMonth(today)
        onDateChange?.(today)
        setOpen(false)
    }

    return (
        <Popover open={open} onOpenChange={setOpen}>
            <PopoverTrigger asChild>
                <Button
                    variant="outline"
                    disabled={disabled}
                    className={cn(
                        "w-[280px] justify-start text-left font-normal",
                        !selectedDate && "text-muted-foreground",
                        className
                    )}
                >
                    <CalendarIcon className="mr-2 h-4 w-4" />
                    {selectedDate ? (
                        format(selectedDate, "yyyy年MM月dd日", { locale: zhTW })
                    ) : (
                        <span>{placeholder}</span>
                    )}
                </Button>
            </PopoverTrigger>
            <PopoverContent
                className="w-auto p-0 bg-white border border-slate-200 shadow-2xl z-[100]"
                align="start"
            >
                <div className="p-3">
                    <YearMonthSelector
                        currentMonth={currentMonth}
                        onMonthChange={setCurrentMonth}
                    />
                </div>
                <div className="px-3 pb-0">
                    <Calendar
                        mode="single"
                        selected={selectedDate}
                        onSelect={handleSelect}
                        month={currentMonth}
                        onMonthChange={setCurrentMonth}
                        initialFocus
                        classNames={{
                            nav: "hidden",
                            month_caption: "hidden",
                            month_grid: "w-full border-collapse",
                            weekdays: "flex",
                            weekday: "text-slate-500 w-9 font-normal text-[0.8rem] text-center", // 星期幾：灰色
                            day: "h-9 w-9 text-center text-sm p-0 font-normal text-slate-900 hover:bg-slate-100 rounded-md", // 日期：黑色
                            day_selected: "bg-slate-900 text-white hover:bg-slate-900 focus:bg-slate-900 !opacity-100", // 選中：黑底白字
                            day_today: "bg-slate-100 text-slate-900 font-bold", // 今天：淺灰底
                            day_outside: "text-slate-400 opacity-30", // 月份外：淡色
                        }}
                    />
                </div>
                <CalendarFooter onClear={handleClear} onToday={handleToday} />
            </PopoverContent>
        </Popover>
    )
}

// ============================================
// 匯出的 DateRangePicker 組件
// ============================================
interface DateRangePickerProps {
    dateRange?: DateRange // 💡 直接用官方定義的型別
    onDateRangeChange?: (range: DateRange | undefined) => void
    placeholder?: string
    className?: string
    disabled?: boolean
}

export function DateRangePicker({
                                    dateRange,
                                    onDateRangeChange,
                                    placeholder = "選擇日期範圍",
                                    className,
                                    disabled = false,
                                }: DateRangePickerProps) {
    const [range, setRange] = React.useState<DateRange | undefined>(dateRange)
    const [currentMonth, setCurrentMonth] = React.useState<Date>(dateRange?.from || new Date())
    const [open, setOpen] = React.useState(false)

    const handleSelect = (selected: DateRange | undefined) => {
        setRange(selected);
        onDateRangeChange?.(selected);
        if (selected?.from && selected?.to) {
            setOpen(false);
        }
    };

    const handleClear = () => {
        setRange({ from: undefined, to: undefined })
        onDateRangeChange?.({ from: undefined, to: undefined })
        setCurrentMonth(new Date())
    }

    const handleToday = () => {
        const today = new Date()
        const newRange = { from: today, to: today }
        setRange(newRange)
        setCurrentMonth(today)
        onDateRangeChange?.(newRange)
        setOpen(false)
    }

    const secondMonth = React.useMemo(() => {
        const date = new Date(currentMonth)
        date.setMonth(date.getMonth() + 1)
        return date
    }, [currentMonth])

    const handleSecondMonthChange = (date: Date) => {
        const newFirstMonth = new Date(date)
        newFirstMonth.setMonth(newFirstMonth.getMonth() - 1)
        setCurrentMonth(newFirstMonth)
    }

    return (
        <Popover open={open} onOpenChange={setOpen}>
            <PopoverTrigger asChild>
                <Button
                    variant="outline"
                    disabled={disabled}
                    className={cn(
                        "w-[320px] justify-start text-left font-normal",
                        !range?.from && "text-muted-foreground",
                        className
                    )}
                >
                    <CalendarIcon className="mr-2 h-4 w-4" />
                    {range?.from ? (
                        range?.to ? (
                            <>
                                {format(range.from, "yyyy/MM/dd", { locale: zhTW })} -{" "}
                                {format(range.to, "yyyy/MM/dd", { locale: zhTW })}
                            </>
                        ) : (
                            format(range.from, "yyyy年MM月dd日", { locale: zhTW })
                        )
                    ) : (
                        <span>{placeholder}</span>
                    )}
                </Button>
            </PopoverTrigger>
            <PopoverContent
                className="w-auto p-0 bg-white border border-slate-200 shadow-2xl z-[100]"
                align="start"
            >
                <div className="flex flex-col md:flex-row">
                    <div className="border-b md:border-b-0 md:border-r">
                        <div className="p-3">
                            <YearMonthSelector
                                currentMonth={currentMonth}
                                onMonthChange={setCurrentMonth}
                            />
                        </div>
                        <div className="px-3 pb-3">
                            <Calendar
                                mode="range"
                                selected={range}
                                onSelect={handleSelect}
                                month={currentMonth}
                                onMonthChange={setCurrentMonth}
                                numberOfMonths={1}
                                classNames={{
                                    nav: "hidden",
                                    month_caption: "hidden",
                                    month_grid: "w-full border-collapse",
                                    weekdays: "flex",
                                    weekday: "text-slate-500 w-9 font-normal text-[0.8rem] text-center", // 星期幾：灰色
                                    day: "h-9 w-9 text-center text-sm p-0 font-normal text-slate-900 hover:bg-slate-100 rounded-md", // 日期：黑色
                                    day_selected: "bg-slate-900 text-white hover:bg-slate-900 focus:bg-slate-900 !opacity-100", // 選中：黑底白字
                                    day_today: "bg-slate-100 text-slate-900 font-bold", // 今天：淺灰底
                                    day_outside: "text-slate-400 opacity-30", // 月份外：淡色
                                }}
                            />
                        </div>
                    </div>
                    <div>
                        <div className="p-3">
                            <YearMonthSelector
                                currentMonth={secondMonth}
                                onMonthChange={handleSecondMonthChange}
                            />
                        </div>
                        <div className="px-3 pb-3">
                            <Calendar
                                mode="range"
                                selected={range}
                                onSelect={handleSelect}
                                month={secondMonth}
                                numberOfMonths={1}
                                classNames={{
                                    nav: "hidden",
                                    month_caption: "hidden",
                                }}
                            />
                        </div>
                    </div>
                </div>
                <CalendarFooter onClear={handleClear} onToday={handleToday} />
            </PopoverContent>
        </Popover>
    )
}
